import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GenerateData {
    public static void main(String[] args) throws IOException {

        int[] sizes = {10000, 20000, 30000, 40000, 50000,
                        60000, 70000, 80000, 90000, 100000};

        int runs = 50; // número de execuções por tamanho
        Random rand = new Random(System.currentTimeMillis());


        for (int size : sizes) {
            String dirName = String.format("dados/n%06d", size);
            Files.createDirectories(Paths.get(dirName));

            for (int run = 1; run <= runs; run++) {
                int[] vetor = new int[size];
                for (int i = 0; i < size; i++) {
                    vetor[i] = rand.nextInt(size * 10);
                }

                Arrays.sort(vetor); // Ordena o vetor

                String fileName = String.format("%s/run_%03d.csv", dirName, run);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    for (int i = 0; i < vetor.length; i++) {
                        writer.write(Integer.toString(vetor[i]));
                        if (i < vetor.length - 1) writer.write(",");
                    }
                }

                System.out.println("Gerado: " + fileName);
            }
        }

        System.out.println("\nConcluído.");
    }
}
