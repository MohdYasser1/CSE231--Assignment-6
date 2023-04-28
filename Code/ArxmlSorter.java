package Code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

//A checked exception that triggers when the file doesn't have the right extension
class NotValidAutosarFileException extends Exception{
    public NotValidAutosarFileException(String message){
        super(message);
    }
}
//A unchecked exception that triggers when the file is empty
class EmptyAutosarFileException extends RuntimeException{
    public EmptyAutosarFileException(String message){
        super(message);
    }
}
//A containers class so we can work on each container in the file 
class Containers implements Comparable<Containers>{
    private String shortname;
    private String content;
    private static int count = 0;

    //Storing the containers contents and updating the counter
    public Containers(String content){
        this.content = content;
        count++;
    }
    //Getting the short name so we can sort it afterwards
    private String getShortName(){
        //returning whats inside shortname
        return content.substring(content.indexOf("<SHORT-NAME>") + 12, content.indexOf("</SHORT-NAME>"));
    }
    public static int numOfContainers(){
        return count;
    }
    //Overding the toString method to print the contents
    @Override
    public String toString(){
        return content;
    }
    //Overriding the compareTo method to be able to sort the files according to the shortname
    @Override
    public int compareTo(Containers cont2){
        return this.getShortName().compareTo(cont2.getShortName());
    }
}
public class ArxmlSorter {
    //A method to input the arxml file
    public static void main(String[] args) {
        try {
            String arxmlfile = args[0];
            //Checking if we have the right extension
            if(!arxmlfile.endsWith(".arxml")){
                throw new NotValidAutosarFileException("This file doesn't has the .arxml extension");
            }
            //Input the file
            File inputfile = new File(arxmlfile);
            //Buffered read to read the entire file
            BufferedReader readfile = new BufferedReader(new FileReader(inputfile));
            StringBuilder data = new StringBuilder();
            String line;
            //Checking if the file is not empty
            if(inputfile.length() == 0){
                throw new EmptyAutosarFileException("The .arxml file is EMPTY");
            }
            while((line = readfile.readLine()) != null){
                data.append(line);
                data.append("\n");
            }
            String arxmlString = data.toString();
            ArrayList<Containers> containers = new ArrayList<Containers>();
            int index = 0;
            while(arxmlString.indexOf("<CONTAINER",index) != -1){
                containers.add(new Containers(arxmlString.substring(index = arxmlString.indexOf("<CONTAINER",index), arxmlString.indexOf("</CONTAINER>", index) + 12)));
                index = arxmlString.indexOf("<CONTAINER", index) + 1;
            }
            Collections.sort(containers);

            File output = new File(arxmlfile.substring(0, arxmlfile.indexOf("."))+ "_mod.arxml");
            if(output.createNewFile()){
                System.out.println("New file created");
            }
            BufferedWriter outputData = new BufferedWriter(new FileWriter(output, true));
            outputData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AUTOSAR>\n    ");
            for(Containers i : containers){
                outputData.append(i.toString());
                outputData.append("\n");
            }
            outputData.append("</AUTOSAR>");
            outputData.close();
            } catch (NotValidAutosarFileException e) {
                System.out.println("Not valid .arxml file: " +e.getMessage()); 
            } catch (FileNotFoundException e){
                System.out.println(e.getMessage()); 
            } catch (EmptyAutosarFileException e){
                System.out.println("Empty arxml file: " +e.getMessage()); 
            } catch (IOException e){
                System.out.println(e.getMessage()); 
            }
           
    }
}

