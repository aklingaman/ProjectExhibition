package main.java.Visualization;

import javafx.util.Pair;
import main.java.Image;
import main.java.util.LinAlg;
import main.java.util.TestRunDataPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Creates a window that shows the test results with each correct and incorrect guess.
 *
 *
 */
public class TestRunViewer {


    //IDEA: left 3/4 of the space is 2 rows of horizontal scroll. Top row shows correct, bottom row shows incorrect guesses, and the label of what it tried to guess it as.
    // Right 1/4 of the screen has some aggregate stats.
    private MatrixViewer matrixViewer;
    private int totalHeight = 740;
    private int totalWidth = 900;
    public TestRunViewer() {
        matrixViewer = new MatrixViewer(MatrixViewer.MatrixViewerPixelMode.GREYSCALE);
        matrixViewer.setScaleFactor(3);
    }



    public void generateTestReport(List<TestRunDataPoint> correctGuesses, List<TestRunDataPoint> wrongGuesses) {


        JSplitPane guesses = createGuessesPanel(correctGuesses,wrongGuesses);
        JPanel stats = createStatsPanel();

        JSplitPane verticalSplit = createOverallPanel(totalWidth,totalHeight);
        verticalSplit.setLeftComponent(guesses);
        verticalSplit.setRightComponent(stats);


        JFrame frame = new JFrame();
        frame.setSize(totalWidth,totalHeight);
        frame.add(verticalSplit);
        frame.setVisible(true);

    }

    private JSplitPane createGuessesPanel(List<TestRunDataPoint> correctGuesses, List<TestRunDataPoint> wrongGuesses) {
        JSplitPane guesses = new JSplitPane();
        guesses.setDividerSize(15);
        guesses.setDividerLocation(totalHeight/2);
        guesses.setOrientation(JSplitPane.VERTICAL_SPLIT);

        JPanel correctGuessesPanel = new JPanel();
        JPanel wrongGuessesPanel = new JPanel();

        for(TestRunDataPoint correctGuess: correctGuesses){
            BufferedImage guessAsImage = matrixViewer.viewMatrix(correctGuess.getImage().data);
            JLabel label = createGuessLabel(guessAsImage, correctGuess);
            correctGuessesPanel.add(label);
        }
        for(TestRunDataPoint wrongGuess : wrongGuesses){
            BufferedImage guessAsImage = matrixViewer.viewMatrix(wrongGuess.getImage().data);
            JLabel label = createGuessLabel(guessAsImage, wrongGuess);
            wrongGuessesPanel.add(label);
        }


        JScrollPane correctScrollPane = new JScrollPane(correctGuessesPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane wrongScrollPane = new JScrollPane(wrongGuessesPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);


        guesses.setTopComponent(correctScrollPane);
        guesses.setBottomComponent(wrongScrollPane);

        return guesses;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel();
        return stats;
    }

    private JSplitPane createOverallPanel(int totalWidth,int totalHeight) {
        JSplitPane verticalSplit = new JSplitPane();
        verticalSplit.setSize(totalWidth, totalHeight);
        verticalSplit.setDividerSize(30); //extract
        verticalSplit.setDividerLocation(totalWidth*7/10); //extract
        verticalSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        return verticalSplit;
    }

    private String generateAccuracyText(TestRunDataPoint dataPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        for(int i = 0; i<dataPoint.getGuess().length; i++) {
            if(i!=dataPoint.getImage().label) {
                sb.append("<p>");
                sb.append(i);
                sb.append(": ");
                sb.append(String.format(java.util.Locale.US, "%.2f", dataPoint.getGuess().get(i)));
                sb.append("</p>");
            } else {
                sb.append("<p style=\"color:Green;\">");
                sb.append(i);
                sb.append(": ");
                sb.append(String.format(java.util.Locale.US, "%.3f", dataPoint.getGuess().get(i)));
                sb.append("</p>");
            }
        }
        sb.append("Cost: ");
        sb.append(String.format(java.util.Locale.US, "%.3f", dataPoint.getCost()));

        sb.append("</html>");
        return sb.toString();
    }

    private JLabel createGuessLabel(BufferedImage guessAsImage, TestRunDataPoint guess) {
        JLabel label = new JLabel(new ImageIcon(guessAsImage));
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setText(generateAccuracyText(guess));
        label.setFont(new Font("Serif", Font.PLAIN, 14));
        return label;
    }










}
