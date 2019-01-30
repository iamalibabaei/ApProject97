package view.gameScene;

import controller.InGameController;
import controller.MenuController;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.buildings.Warehouse;
import models.buildings.Well;
import models.exceptions.InsufficientResourcesException;
import models.exceptions.IsWorkingException;
import models.interfaces.Time;
import models.objects.Grass;
import models.objects.Point;
import models.objects.animals.Animal;
import view.MainView;
import view.gameScene.truck.TruckView;
import view.menu.SpriteAnimation;
import view.utility.AddressConstants;
import view.utility.Utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class InGameView extends Scene implements Time
{
    private static InGameView instance = new InGameView();

    public static InGameView getInstance() {
        return instance;
    }

    private Group root;
    private Text money;

    private InGameView() {
        super(new Group(), MainView.WIDTH, MainView.HEIGHT);
        root = (Group) getRoot();
        build();
    }

    private void build() {
        root.getChildren().clear();
        root.getChildren().addAll(GameBackground.getInstance(), MapView.getInstance(), WarehouseScene.getInstance());
        wellGraphic();
        warehouseGraphic();
        moneyGraphic();
        truckGraphic();
        root.getChildren().addAll(TruckView.getInstance());
    }

    private void truckGraphic() {
        Image truckImage = null;
        try {
            truckImage = new Image(new FileInputStream(
                    AddressConstants.TRUCK_PICTURE_ROOT + Warehouse.getInstance().getLevel() + ".png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(truckImage);
        StackPane truckPane = new StackPane();
        truckPane.getChildren().addAll(imageView);
        truckPane.relocate(MainView.WIDTH / 2 - truckImage.getWidth() * 2, MainView.HEIGHT - truckImage.getHeight());
        root.getChildren().addAll(truckPane);
        truckPane.setOnMouseClicked(event -> openTruck());


    }

    public void getMoney(){
        money.setText(Integer.toString(InGameController.getInstance().getMoney()));
    }

    private void wellGraphic() {
        int XValue = (int) (MainView.WIDTH * 0.5), YValue = (int) (MainView.HEIGHT / 7);

        ImageView wellImageView = new ImageView(Utility.getImage(AddressConstants.WELL_PICTURE_ROOT +
                Well.getInstance().getLevel() + ".png"));
        wellImageView.relocate(XValue, YValue);
//        wellImageView.setViewport(new Rectangle2D(0, 0, (int) (wellImageView.getImage().getWidth() / 4),
//                (int) wellImageView.getImage().getHeight() / 4));
        root.getChildren().addAll(wellImageView);

        SpriteAnimation wellSpriteAnimation = new SpriteAnimation(wellImageView, Duration.millis(1250), 16, 4,
                0, 0, (int) (wellImageView.getImage().getWidth() / 4),
                (int) wellImageView.getImage().getHeight() / 4);
//        wellSpriteAnimation.play();
        wellSpriteAnimation.stop();
        wellImageView.setOnMouseClicked(event -> {
            try {
                MenuController.getInstance().refillWell();
            } catch (IsWorkingException | InsufficientResourcesException e) {
                MainView.getInstance().showExceptions(e, XValue, YValue);
            }
            wellSpriteAnimation.setCycleCount(Well.getInstance().REFILL_TIME[Well.getInstance().getLevel()]);
            wellSpriteAnimation.playFromStart();
            wellSpriteAnimation.setOnFinished(event1 -> {
                wellSpriteAnimation.stop();
            });
        });
    }

    private void moneyGraphic() {
        money = new Text(Integer.toString(InGameController.getInstance().getMoney()));
        money.setFill(Color.YELLOW);
        money.setFont(Font.font(30));
        StackPane pane = new StackPane();
        pane.getChildren().addAll(money);
        pane.setAlignment(Pos.CENTER);
        pane.relocate(MainView.WIDTH * 0.65, MainView.HEIGHT / 8.5);
        root.getChildren().addAll(pane);
    }

    @Override
    public void nextTurn() {
        getMoney();
        MapView.getInstance().nextTurn();
        GameBackground.getInstance().nextTurn();
    }





    public void addAnimal(Animal animal, Point location) {
        Text text = animal.getText();
        text.relocate((location.getX() + 325) *  MapView.WIDTH_BASE, location.getY() * MapView.HEIGHT_BASE);
        MapView.getInstance().getChildren().addAll(text);
    }

    public void addGrass(Grass entity, Point location) {
        Text text = entity.getText();
        text.relocate((location.getX() + 325) *  MapView.WIDTH_BASE, location.getY() * MapView.HEIGHT_BASE);
        MapView.getInstance().getChildren().addAll(text);
    }
    public void closeTruck() {
        TruckView.getInstance().setVisible(false);
    }
    public void openTruck(){
        TruckView.getInstance().updateInformation();
        TruckView.getInstance().setVisible(true);
    }

    public void closeWarehouse() {
        //root.getChildren().remove(WarehouseScene.getInstance());
        WarehouseScene.getInstance().setVisible(false);
    }

    private void openWarehouse() {
        WarehouseScene.getInstance().UpdateInformation();
        WarehouseScene.getInstance().setVisible(true);

    }

    private void warehouseGraphic() {
        Image warehouseImage = null;
        try {
            warehouseImage = new Image(new FileInputStream(
                    AddressConstants.WAREHOUSE_PICTURE_ROOT + Warehouse.getInstance().getLevel() + ".png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(warehouseImage);

        StackPane warehousePane = new StackPane();
        warehousePane.getChildren().addAll(imageView);
        warehousePane.relocate((MainView.WIDTH - warehouseImage.getWidth()) / 2, MainView.HEIGHT - 2 * warehouseImage.getHeight());
        root.getChildren().addAll(warehousePane);
        warehousePane.setOnMouseClicked(event -> openWarehouse());


    }
}
