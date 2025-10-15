import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.imageio.ImageIO;

public class SobelBenchmark {

    // Pastas de entrada (iguais às do seu print)
    private static final List<String> CATEGORIAS = Arrays.asList(
        "Animais", "Carro", "Jogador de Futebol", "Praia"
    );

    private static final String RAIZ_SAIDA  = "Saida";    // igual ao nome que aparece no Explorer
    private static final String RAIZ_REPORT = "Reports";  // igual ao Explorer

    // Filtro de imagens
    private static final FileFilter IMG_FILTER = f -> {
        if (!f.isFile()) return false;
        String n = f.getName().toLowerCase();
        return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg");
    };

    // ---------- Sobel ----------
    public static BufferedImage aplicarSobel(BufferedImage img) {
        final int w = img.getWidth(), h = img.getHeight();
        int[] gray = new int[w * h];
        for (int y = 0, idx = 0; y < h; y++) {
            for (int x = 0; x < w; x++, idx++) {
                int argb = img.getRGB(x, y);
                int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
                gray[idx] = (int)(0.299*r + 0.587*g + 0.114*b);
            }
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        int[] gx = {-1,0,1, -2,0,2, -1,0,1};
        int[] gy = {-1,-2,-1, 0,0,0, 1,2,1};
        for (int y = 1; y < h-1; y++) {
            for (int x = 1; x < w-1; x++) {
                int sx = 0, sy = 0, k = 0;
                for (int j = -1; j <= 1; j++) {
                    int row = (y + j) * w;
                    for (int i = -1; i <= 1; i++, k++) {
                        int p = gray[row + (x + i)];
                        sx += gx[k]*p; sy += gy[k]*p;
                    }
                }
                int mag = (int)Math.min(255, Math.hypot(sx, sy));
                int rgb = (mag << 16) | (mag << 8) | mag;
                out.setRGB(x, y, rgb);
            }
        }
        return out;
    }

    // ---------- Utilidades ----------
    private static void ensureDir(Path p) throws IOException { Files.createDirectories(p); }

    private static List<File> coletarTodasImagens() {
        List<File> out = new ArrayList<>();
        for (String cat : CATEGORIAS) {
            File dir = new File(cat);
            if (!dir.isDirectory()) {
                System.out.printf("[AVISO] Pasta '%s' não existe. Pulando.%n", cat);
                continue;
            }
            File[] files = dir.listFiles(IMG_FILTER);
            if (files == null || files.length == 0) {
                System.out.printf("[AVISO] Pasta '%s' não tem imagens .png/.jpg/.jpeg.%n", cat);
                continue;
            }
            out.addAll(Arrays.asList(files));
        }
        out.sort(Comparator.comparing(File::getAbsolutePath));
        System.out.printf("[INFO] Imagens encontradas: %d%n", out.size());
        return out;
    }

    private static void processSerial(List<File> files, Path raizOut, boolean salvar) throws Exception {
        if (salvar) ensureDir(raizOut);
        for (File f : files) {
            BufferedImage img = ImageIO.read(f);
            if (img == null) continue;
            BufferedImage sobel = aplicarSobel(img);
            if (salvar) {
                String cat = f.getParentFile().getName();
                Path outDir = raizOut.resolve(cat); ensureDir(outDir);
                Path out = outDir.resolve(stripExt(f.getName()) + ".png");
                ImageIO.write(sobel, "png", out.toFile());
            }
        }
    }

    // ---------- Paralelo com THREADS PURAS (sem ExecutorService) ----------
    private static void processParallel(List<File> files, Path raizOut, int threads, boolean salvar) throws Exception {
        if (salvar) ensureDir(raizOut);

        List<Thread> workers = new ArrayList<>(threads);
        // captura a primeira falha para propagar depois do join
        final java.util.concurrent.atomic.AtomicReference<Throwable> firstErr =
                new java.util.concurrent.atomic.AtomicReference<>();

        for (int t = 0; t < threads; t++) {
            final int start = t;
            Thread th = new Thread(() -> {
                for (int i = start; i < files.size(); i += threads) {
                    if (firstErr.get() != null) return; // alguém já falhou
                    File f = files.get(i);
                    try {
                        BufferedImage img = ImageIO.read(f);
                        if (img == null) continue;
                        BufferedImage sobel = aplicarSobel(img);
                        if (salvar) {
                            String cat = f.getParentFile().getName();
                            Path outDir = raizOut.resolve(cat); ensureDir(outDir);
                            Path out = outDir.resolve(stripExt(f.getName()) + ".png");
                            ImageIO.write(sobel, "png", out.toFile());
                        }
                    } catch (Throwable e) {
                        firstErr.compareAndSet(null, e);
                        return;
                    }
                }
            }, "sobel-w" + t);
            th.start();
            workers.add(th);
        }

        for (Thread th : workers) th.join();
        if (firstErr.get() != null) throw new Exception("Falha em worker", firstErr.get());
    }

    private static String stripExt(String n) {
        int i = n.lastIndexOf('.');
        return (i >= 0) ? n.substring(0, i) : n;
    }

    private static void warmUp() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 200; i++) aplicarSobel(img);
    }

    // ---------- Relatórios ----------
    private static void writeReport(Path txt, int totalImgs, double tSerial, double tT2,
                                    double[] tempos, double[] speedup, int maxThreads) throws IOException {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String javaVer = System.getProperty("java.version");
        String os      = System.getProperty("os.name") + " " + System.getProperty("os.arch");
        int cores      = Runtime.getRuntime().availableProcessors();

        StringBuilder sb = new StringBuilder();
        sb.append("Trabalho Prático: Implementação de Aplicação Paralela em Java\n");
        sb.append("Problema: Filtro de Sobel sobre conjunto de imagens\n\n");
        sb.append("Ambiente\n");
        sb.append(String.format("  Data/Hora: %s%n", data));
        sb.append(String.format("  SO: %s%n", os));
        sb.append(String.format("  Java: %s%n", javaVer));
        sb.append(String.format("  Núcleos lógicos: %d%n", cores));
        sb.append(String.format("  Pastas: %s%n", String.join(", ", CATEGORIAS)));
        sb.append(String.format("  Total de imagens: %d%n%n", totalImgs));
        sb.append("Resultados (exigência)\n");
        sb.append(String.format("  Serial:           %.3f s%n", tSerial));
        sb.append(String.format("  Paralelo (2thr):  %.3f s%n", tT2));
        sb.append(String.format("  Speed-up(2thr):   %.2fx%n%n", (tT2>0? tSerial/tT2 : 0)));

        sb.append("Desafio — speed-up 1..N\n");
        sb.append("  threads,tempo_s,speedup\n");
        for (int t = 1; t < tempos.length; t++) {
            sb.append(String.format(Locale.US, "  %d,%.6f,%.6f%n", t, tempos[t], speedup[t]));
        }
        Files.writeString(txt, sb.toString(), StandardCharsets.UTF_8);
    }

    private static void writeCSV(Path csv, double[] tempos, double[] speedup) throws IOException {
        StringBuilder sb = new StringBuilder("threads,tempo_s,speedup\n");
        for (int t = 1; t < tempos.length; t++) {
            sb.append(String.format(Locale.US, "%d,%.6f,%.6f%n", t, tempos[t], speedup[t]));
        }
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        int maxThreads = 16;
        for (int i = 0; i < args.length - 1; i++)
            if ("--max-threads".equals(args[i]))
                try { maxThreads = Math.max(1, Integer.parseInt(args[i+1])); } catch (Exception ignored) {}

        System.out.println("[INFO] Iniciando benchmark...");
        List<File> files = coletarTodasImagens();
        if (files.isEmpty()) {
            System.out.println("[ERRO] Nenhuma imagem encontrada nas pastas informadas.");
            return;
        }

        Files.createDirectories(Path.of(RAIZ_REPORT));
        warmUp();

        System.out.println("[INFO] Executando versão SERIAL...");
        long s0 = System.nanoTime();
        processSerial(files, Path.of(RAIZ_SAIDA, "serial"), true);
        double tSerial = (System.nanoTime() - s0) / 1e9;
        System.out.printf("[OK] Serial concluído em %.3f s%n", tSerial);

        System.out.println("[INFO] Executando versão PARALELA (2 threads)...");
        long p0 = System.nanoTime();
        processParallel(files, Path.of(RAIZ_SAIDA, "t2"), 2, true);
        double tT2 = (System.nanoTime() - p0) / 1e9;
        System.out.printf("[OK] Paralelo(2) concluído em %.3f s%n", tT2);

        double[] tempos  = new double[maxThreads + 1];
        double[] speedup = new double[maxThreads + 1];
        tempos[1] = tSerial; speedup[1] = 1.0;

        System.out.printf("[INFO] Rodando desafio 1..%d threads (sem salvar saída)...%n", maxThreads);
        for (int t = 1; t <= maxThreads; t++) {
            long b0 = System.nanoTime();
            processParallel(files, Path.of(RAIZ_SAIDA, "benchmark_t" + t), t, false);
            tempos[t] = (System.nanoTime() - b0) / 1e9;
            speedup[t] = tSerial / tempos[t];
            System.out.printf("  t=%2d -> %.3f s | speed-up %.2fx%n", t, tempos[t], speedup[t]);
        }

        Path txt = Path.of(RAIZ_REPORT, "relatorio.txt");
        Path csv = Path.of(RAIZ_REPORT, "speedup.csv");
        writeReport(txt, files.size(), tSerial, tT2, tempos, speedup, maxThreads);
        writeCSV(csv, tempos, speedup);

        System.out.println("[OK] Relatório: " + txt.toAbsolutePath());
        System.out.println("[OK] CSV:       " + csv.toAbsolutePath());
    }
}
