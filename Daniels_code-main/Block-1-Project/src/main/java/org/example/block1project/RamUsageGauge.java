package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.util.Duration;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

public class RamUsageGauge {

    private Arc usedRamArc;
    private Arc freeRamArc;
    private GlobalMemory memory;
    private Text usedRamText;
    private Text freeRamText;
    private Label ramUsageLabel;

    // initialize gauge and timeline
    public RamUsageGauge() {
        SystemInfo systemInfo = new SystemInfo();
        memory = systemInfo.getHardware().getMemory();

        // setup arcs for RAM display
        usedRamArc = new Arc(0, 0, 150, 150, 180, 0);
        usedRamArc.setType(ArcType.OPEN);
        usedRamArc.setStroke(Color.web("#ffa000"));
        usedRamArc.setStrokeWidth(30);
        usedRamArc.setFill(Color.TRANSPARENT);

        freeRamArc = new Arc(0, 0, 150, 150, 360, 0);
        freeRamArc.setType(ArcType.OPEN);
        freeRamArc.setStroke(Color.GRAY);
        freeRamArc.setStrokeWidth(30);
        freeRamArc.setFill(Color.TRANSPARENT);

        usedRamText = new Text("used: 0 gb");
        freeRamText = new Text("free: 0 gb");
        usedRamText.setFill(Color.BLACK);
        freeRamText.setFill(Color.BLACK);

        ramUsageLabel = new Label("ram usage %");
        ramUsageLabel.setTextFill(Color.BLACK);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateRamUsage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // update arc lengths and text
    private void updateRamUsage() {
        long totalMemory = memory.getTotal();
        long freeMemory = memory.getAvailable();
        long usedMemory = totalMemory - freeMemory;
        double usedPercentage = ((double) usedMemory / totalMemory) * 180;

        Platform.runLater(() -> {
            usedRamArc.setLength(-usedPercentage);
            freeRamArc.setLength(180 - usedPercentage);

            usedRamText.setText(String.format("used: %.2f gb", usedMemory / (1024.0 * 1024 * 1024)));
            freeRamText.setText(String.format("free: %.2f gb", freeMemory / (1024.0 * 1024 * 1024)));

            // center text
            usedRamText.setX(290 - usedRamText.getLayoutBounds().getWidth() / 2);
            freeRamText.setX(290 - freeRamText.getLayoutBounds().getWidth() / 2);
            usedRamText.setY(180);
            freeRamText.setY(210);
        });
    }

    // return VBox with gauge and labels
    public Pane getRamUsagePane() {
        VBox vbox = new VBox(10);
        Region spacer = new Region();
        spacer.setPrefHeight(40);

        vbox.getChildren().addAll(spacer, ramUsageLabel, usedRamText, freeRamText);
        Pane arcPane = new Pane();
        arcPane.getChildren().addAll(freeRamArc, usedRamArc);

        // center arcs in pane
        double centerX = 250;
        double centerY = 200;
        usedRamArc.setTranslateX(centerX);
        usedRamArc.setTranslateY(centerY);
        freeRamArc.setTranslateX(centerX);
        freeRamArc.setTranslateY(centerY);

        arcPane.setPrefSize(600, 300);
        arcPane.setStyle("-fx-alignment: center;");

        vbox.getChildren().add(arcPane);
        vbox.setStyle("-fx-alignment: center;");

        return vbox;
    }
}
