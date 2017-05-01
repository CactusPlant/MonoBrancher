import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

/**
 * Created by Cactus on 4/29/2017.
 */
public class Main extends Application {

    public static void main(String[] args){launch(args);}
    // Variable Declaration
    Stage window = null;
    wordProcessor wp = new wordProcessor();
    sqlReader reader = sqlReader.getInstance();
    BorderPane mainLayout = new BorderPane();
    VBox top = new VBox();
    VBox center = new VBox();
    HBox bottom = new HBox();
    TreeItem<String> rootItem = new TreeItem<String>("Root");
    TreeView left = new TreeView(rootItem);
    String previous;


    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        // Set host nodes for layout
        mainLayout.setLeft(left);
        mainLayout.setTop(top);
        mainLayout.setCenter(center);
        mainLayout.setBottom(bottom);

        //Creates bottom field and loads into the HBox bottom
        TextField stringEnter = new TextField();
        stringEnter.setPromptText("Enter or Paste Japanese Text");
        Button button = new Button("Load String");
        button.setOnAction(event -> setSentence(top, wp.breakUpSentence(stringEnter.getText())));

        bottom.getChildren().addAll(stringEnter, button);
        bottom.setPadding(new Insets(10));
        top.setPadding(new Insets(10));
        bottom.setSpacing(10);

        //Creates scene and handles window parameters
        window.setScene(new Scene(mainLayout));
        window.setHeight(600);
        window.setWidth(800);
        window.show();


    }
    //Gets Sentence from source that has been parsed and creates labels for each word.
    //Applies series of Labels in HBoxes dependant of sentence length
    //Assigns an EventHandler to each label so when clicked will create definition of newly clicked word
    private void setSentence(VBox labelBox, ArrayList<String> stringToInsert) {
        //Stops the function if String is null
        if (stringToInsert == null) {
            return;
        }
        labelBox.getChildren().clear();

        //Generates new HBox if label / word limit(25) is hit to create word wrapping
        int numLabels = 25;
        HBox recurBox = null;
        for (String s : stringToInsert) {
            Label word = new Label(s);
            if (numLabels % 25 == 0) {
                recurBox = new HBox();
                labelBox.getChildren().add(recurBox);
            }
            numLabels++;


            word.getStylesheets().add("default.css");
            setKnownStyle(word);



                word.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> word.setStyle("-fx-background-color:#dae7f3;"));
                word.addEventFilter(MouseEvent.MOUSE_EXITED, event -> setKnownStyle(word));
                word.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.isControlDown()){
                        try{
                            reader.toggleKnown(DriverManager.getConnection("jdbc:sqlite:res\\db\\known.db"),
                                    word.getText());
                        }catch(Exception e){e.printStackTrace();}

                    }
                    else {
                        try {
                            Connection con = DriverManager.getConnection("jdbc:sqlite:res\\db\\daijisen.db");
                            setSentence(center, wp.breakUpSentence(String.valueOf(reader.getDefinition(con, wp.dictionaryForm(word.getText())))));

                            addToTree(word.getText(), previous, rootItem);
                            previous = word.getText();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                recurBox.getChildren().add(word);

            }

    }
    //Assigns proper CSS layout for known/unknown words
    private void setKnownStyle(Label word){try {
        String s = word.getText();
        if (reader.isKnown(DriverManager.getConnection("jdbc:sqlite:res\\db\\known.db"), s)) {
            word.setStyle("-fx-background-color: #00CC66;");
        } else {
            word.setStyle("-fx-background-color: #F0F0F0;");
        }
    } catch (Exception e) {
        e.printStackTrace();}

    }
    //Adds selected word to TreeView(left) when definition is loaded to center.
    //Word Recursively added to last last word clicked.
    private void addToTree(String name, String prev, TreeItem<String> root){
        for (TreeItem<String> branch: root.getChildren()){
            if (!branch.getChildren().isEmpty()){addToTree(name, prev, branch);return;}
            if (branch.getValue().equals(prev)){
                TreeItem<String> temp = new TreeItem<String>();
                temp.setValue(name);
                branch.getChildren().add(temp);
                return;
                }
            }
        TreeItem<String> temp = new TreeItem<String>();
        temp.setValue(name);
        root.getChildren().add(temp);


    }



}
