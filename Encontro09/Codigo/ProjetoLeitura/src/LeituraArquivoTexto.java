import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LeituraArquivoTexto {

    public void executar(){
        String caminhoArquivo = "e:\\temp\\enc8ex01.c";
        try {
            FileReader fileReader = new FileReader(caminhoArquivo);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                System.out.println(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
