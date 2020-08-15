package com.leonel;

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}
import java.io.*;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) throws InterruptedException{
        String mode = "enc"; int key = 0; String data = ""; String outputPath = null;
        String alg = "shift"; String inputPath = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-mode")) {
                mode = args[i + 1];
            }
            else if(args[i].equals("-alg")){
                alg = args[i +1];
            }
            else if (args[i].equals("-key")) {
                key = Integer.parseInt(args[i + 1]);
            }
            else if (args[i].equals("-data")) {
                data = args[i + 1];
            }
            else if(args[i].equals("-in")){
                inputPath = args[i + 1];
            }
            else if (args[i].equals("-out")) {
                outputPath = args[i + 1];
            }
        }
        ExcecuteEncryption encryption = new ExcecuteEncryption();
        if(inputPath != null) {
            try {
                data = readFile(inputPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        switch (mode) {
            case "enc":
                data = encryption.makeEncryption(alg, data, key);
                break;
            case "dec":
                data = encryption.makeDecryption(alg, data, key);
                break;
            default:
                System.out.println("Option no valid");
        }
        System.out.print(data);
        if(outputPath != null){
            try {
                writeFile(outputPath, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.print(data);
        }
        //mode - enc/dec
        //alg - shift/unicode
        //path - file/standard

    }
    static String readFile(String path) throws FileNotFoundException {
        String message = "";
        File file = new File(path);
        Scanner sc = new Scanner(file);
        message = sc.nextLine();
        sc.close();
        return message;
    }

    static void writeFile(String path, String message) throws IOException {
        File file = new File(path);
        FileWriter writer = new FileWriter(file);
        writer.write(message);
        writer.close();
    }
}
//Working with encryptation-decryptation
abstract class Encrytion {
    abstract String encryption(String input, int key);
    abstract String decryption(String input, int key);
}
//shift
class ShiftEncryption extends Encrytion {
    char[] abc = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    String encryption(String input, int key) {
        String message = "";
        for(int i = 0; i < input.length(); i++){
            char character = input.charAt(i);
            if(character == ' ' || character == '!'){
                message += character;
            }
            else{
                message += shiftCharacter(character, key);
            }
        }
        return message;
    }
    private String shiftCharacter(char character, int key) {
        int lastPostition = abc.length-1;
        String message = "";
        for(int j=0; j< abc.length; j++){
            if(character == abc[j]){
                if(j+key > lastPostition){
                    int excededPositions = key - (lastPostition-j);
                    if(character == Character.toUpperCase(character)){
                        message +=  Character.toUpperCase(abc[excededPositions-1]);
                    }
                    else {
                        message += abc[excededPositions-1];
                    }
                }
                else{
                    message += abc[j+key];
                }
            }
        }
        return message;
    }
    private String invertShiftCharacter(char character, int key) {
        String message = "";
        int firstPosition = 0;
        int lastPosition = abc.length-1;
        for(int j=0; j< abc.length; j++){
            if(character == abc[j]){
                if(j-key < firstPosition){ //1-5 = -4
                    int excededPositions = j-key;
                    if(character == Character.toUpperCase(character)){
                        message +=  Character.toUpperCase(abc[lastPosition+excededPositions]);
                    }
                    else {
                        message += abc[lastPosition+excededPositions];
                    }
                }
                else{
                    message += abc[j-key];
                }
            }
        }
        return message;
    }
    String decryption(String input, int key) {
        String message = "";
        for(int i = 0; i < input.length(); i++){
            char character = input.charAt(i);
            if(character == ' ' || character == '!'){
                message += character;
            }
            else{
                message += invertShiftCharacter(character, key);
            }
        }
        return message;
    }

}
//unicode
class UnicodeEncryption extends Encrytion{
    //encriptando
    String encryption(String input, int key) {
        String message = "";
        for (int i = 0; i < input.length(); i++) {
            int position = input.codePointAt(i);
            int index = position + key;
            char character = (char) index;
            message += character;
        }
        return message;
    }
    //desencriptando
    String decryption(String input, int key) {
        String message = "";
        for (int i = 0; i < input.length(); i++) {
            int position = input.codePointAt(i);
            int index = position - key;
            char character = (char) index;
            message += character;
        }
        return message;
    }
}

//factories for instances objects
abstract class FactoryEncryption {
    Encrytion typeOfEncryp;
    abstract Encrytion createEncryption(String type);

    String makeEncryption(String type, String input, int key) {
        typeOfEncryp = createEncryption(type);
        String message = typeOfEncryp.encryption(input, key);
        return message;
    }

    String makeDecryption(String type, String input, int key) {
        typeOfEncryp = createEncryption(type);
        String message = typeOfEncryp.decryption(input, key);
        return message;
    }
}

class ExcecuteEncryption extends FactoryEncryption{
    @Override
    Encrytion createEncryption(String type) {
        switch (type) {
            case "shift":
                return new ShiftEncryption();
            case "unicode":
                return new UnicodeEncryption();
            default:
                return null;
        }
    }
}
