package CheckItemsController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TransitionHelper {

    void spinnerInit(Spinner<Integer> spinner){
        final int initialValue = 1;
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, initialValue);
        spinner.setValueFactory(valueFactory);
    }



    void barcodeItemsFadeTransition(Spinner spinner, JFXTextField textField){
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), spinner);
        FadeTransition fadeTransition2 = new FadeTransition(Duration.millis(duration), textField);
        fadeTransition2.setFromValue(initial);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition2.setToValue(end);
        fadeTransition.play();
        fadeTransition2.play();
    }


    void translateButtons(JFXButton but1, JFXButton but2, int direction){
        int duration = 500;
        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), but1);
        TranslateTransition transition2 = new TranslateTransition(Duration.millis(duration), but2);
        transition.setByY(direction);
        transition2.setByY(direction);
        transition.play();
        transition2.play();
    }

    void fadeTransition(Object object){
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), (Node)object);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition.play();
    }

    void fadeTransitionNewStudentObjects(Label email, JFXTextField emailField){
        fadeTransition(email);
        fadeTransition(emailField);
    }

    void translateExtendedStudentItems(Label course, Label prof, Label due, JFXTextField courseT, JFXTextField profT, JFXDatePicker dueT, JFXCheckBox box, JFXButton butt, JFXButton butt2){
        int direction = 35;
        int direction2 = 10;
        course.setTranslateY(direction);
        prof.setTranslateY(direction);
        due.setTranslateY(direction);
        courseT.setTranslateY(direction);
        profT.setTranslateY(direction);
        dueT.setTranslateY(direction);
    }

    void faultyBoxFadeTransition(TextArea faulty, int direction){
        int initial = 0;
        int end = 1;
        int duration = 500;
        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), faulty);
        transition.setByY(direction);
        transition.play();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration),faulty);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition.play();
    }



    void faultyTransition(JFXCheckBox faulty, JFXButton submit, JFXButton reset, int direction){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 500;
        transitions.add(new TranslateTransition(Duration.millis(duration), faulty));
        transitions.add(new TranslateTransition(Duration.millis(duration), submit));
        transitions.add(new TranslateTransition(Duration.millis(duration), reset));
        translateList(transitions, direction);
    }

    void translateNewStudentItems(Label barcode, Label quantity, JFXTextField barcodeField, JFXTextField quantityField, JFXCheckBox box, JFXButton submit, JFXButton reset){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 500;
        int direction = 50;
        transitions.add(new TranslateTransition(Duration.millis(duration), barcode));
        transitions.add(new TranslateTransition(Duration.millis(duration), quantity));
        transitions.add(new TranslateTransition(Duration.millis(duration), barcodeField));
        transitions.add(new TranslateTransition(Duration.millis(duration), quantityField));
        transitions.add(new TranslateTransition(Duration.millis(duration), box));
        transitions.add(new TranslateTransition(Duration.millis(duration), submit));
        transitions.add(new TranslateTransition(Duration.millis(duration), reset));
        translateList(transitions, direction);
    }

    void translateBarcodeItems(JFXButton button1, JFXButton button2, JFXCheckBox box1, JFXCheckBox box2, int direction){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 1;
        transitions.add(new TranslateTransition(Duration.millis(duration), button1));
        transitions.add(new TranslateTransition(Duration.millis(duration), button2));
        transitions.add(new TranslateTransition(Duration.millis(duration), box1));
        transitions.add(new TranslateTransition(Duration.millis(duration), box2));
        translateList(transitions, direction);
    }

    private void translateList(List<TranslateTransition> items, int direction){
        for (TranslateTransition transition : items) {
            transition.setByY(direction);
            transition.play();
        }
    }
}
