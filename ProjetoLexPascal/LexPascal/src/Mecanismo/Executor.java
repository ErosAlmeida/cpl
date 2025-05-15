package Mecanismo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Executor {

    private final String captureComment = "\\/\\/.*|\\(\\*(?:.|\\s)*\\*\\)";
    private final String captureNumbers = "(?<!\\w)(?:-?\\d+(?:\\.\\d+)?)(?!\\w)";
    private final String captureLiteral = "'(?:[^']|'')*'";
    private final String captureWords   = "\\w+";
    private final String captureCharacters = "(?::=|>=|<=|<>|>|<|=|\\+|\\-|\\*|\\/|[:;,.()\\[\\]{}])";
    private final String captureIdentifier = "^[A-Za-z][A-Za-z0-9_]*$";
    private String capture;

    private BufferedReader reader;
    private ArrayList<String> bufferPrimario;
    private ArrayList<String> bufferSecundario;

    private HashMap<String, Token> tabelaSimbolosPrograma;

    private boolean IsNumber(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureNumbers);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsLiteral(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureLiteral);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsCharacter(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureCharacters);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsIdentifier(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureIdentifier);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    public void CarregarArquivo(){
        System.out.println("----------------------------------------");
        System.out.println("##### Carregar Arquivo Pascal #####");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o diretório do arquivo: ");
        String diretorio = scanner.next();
        System.out.print("Digite o nome do arquivo (com extensão .pas): ");
        String nomeArquivo = scanner.next();
        String caminhoCompleto = diretorio + "/" + nomeArquivo;
        this.CarregarBufferPrimario(caminhoCompleto);
        scanner.close();        
    }

    public void CarregarArquivo(String caminhoCompleto){
        System.out.println("----------------------------------------");
        System.out.println("##### Carregar Arquivo Pascal #####");
        this.CarregarBufferPrimario(caminhoCompleto);
    }

    private void CarregarBufferPrimario(String caminhoCompleto){
        this.reader = null;
        try {
            this.reader = new BufferedReader(new FileReader(caminhoCompleto));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public void ProcessarBufferPrimario(){
        this.bufferPrimario = new ArrayList<>();
        try {
            String linha;
            while ((linha = this.reader.readLine()) != null) {
                bufferPrimario.add(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar o arquivo: " + e.getMessage());
                }
            }
        }
    }

    public void ImprimirBufferPrimario(){
        System.out.println("----------------------------------------");
        System.out.println("##### Conteúdo do Buffer primário: #####");
        for (String texto : this.bufferPrimario) {
            System.out.println(texto);
        }
        System.out.println("----------------------------------------");        
    }

    public void ProcessarBufferSecundario()
    {        
        this.capture = captureComment.concat("|")
            .concat(captureNumbers).concat("|")
            .concat(captureLiteral).concat("|")
            .concat(captureWords).concat("|")
            .concat(captureCharacters);

        this.bufferSecundario = new ArrayList<>();

        Pattern pattern = Pattern.compile(this.capture);

        for (String texto : bufferPrimario) {
            Matcher matcher = pattern.matcher(texto);
            while(matcher.find()){
                String lexema = matcher.group();
                if (this.bufferSecundario.contains(lexema) == false){
                    this.bufferSecundario.add(lexema);
                }
            }
        }
        bufferSecundario.removeIf(value -> value.startsWith("//") || value.startsWith("(*"));
    } 
    
    public void ImprimirBufferSecundario(){
        System.out.println("----------------------------------------");
        System.out.println("##### Conteúdo do Buffer secundário: #####");
        for (String texto : this.bufferSecundario) {
            System.out.println(texto);
        }
        System.out.println("----------------------------------------");        
    }

   public void AnalisarMontandoTabelaSimbolos(){
        this.tabelaSimbolosPrograma = new HashMap<>();
        TabelaSimbolosLinguagem tblSimbolosLinguagem = new TabelaSimbolosLinguagem();
        for (String texto : this.bufferSecundario) {
            String chave = texto.toUpperCase();
            Token valor = null;
            if (tblSimbolosLinguagem.getTabela().containsKey(texto.toLowerCase())){
                
                valor = tblSimbolosLinguagem.getTabela().get(texto.toLowerCase());
                valor.setToken(texto);
                
            }
            else{
                if (this.IsNumber(texto)){
                    valor = new Token(texto, texto, tblSimbolosLinguagem.getNumber(), "Valor Numérico", 0);
                }
                else if (this.IsLiteral(texto)){
                    valor = new Token(texto, texto, tblSimbolosLinguagem.getLiteral(), "Literal", 0);
                }
                else if (this.IsCharacter(texto)){
                    valor = new Token(texto, texto, tblSimbolosLinguagem.getSimbol(), "Caracter Especial", 0);
                }
                else if (this.IsIdentifier(texto)){
                    valor = new Token(texto, texto, tblSimbolosLinguagem.getIdentifier(), "Identificador", 0);
                }
                else{
                    valor = new Token(texto, texto, tblSimbolosLinguagem.getUndefined(), "Não pertence a linguagem", 0);
                }
            }
            this.tabelaSimbolosPrograma.put(chave, valor);
        }
    }

    public void ImprimirTabelaSimbolosPrograma(){
        System.out.println("----------------------------------------");
        System.out.println("##### Tabela da Análise Léxica: #####");
        Integer contador = 1;
        for (Map.Entry<String,Token> pair : this.tabelaSimbolosPrograma.entrySet()) {
            System.out.printf("%3s", contador.toString());
            System.out.printf(" | %20s", pair.getKey());
            System.out.printf(" | %30s \n", pair.getValue().toString());
            contador++;
        }
        System.out.println("----------------------------------------");  
    }
}