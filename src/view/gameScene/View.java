package view.gameScene;

import controller.InGameController;
import controller.MenuController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.buildings.Warehouse;
import models.buildings.Well;
import models.interfaces.Time;
import models.objects.Grass;
import models.objects.Point;
import models.objects.animals.Animal;
import models.transportation.Helicopter;
import models.transportation.Truck;
import view.MainView;
import view.SceneBuilder;
import view.utility.SoundPlayer;
import view.utility.SpriteAnimation;
import view.utility.Utility;
import view.utility.constants.PictureAddresses;
import view.utility.constants.SoundAddresses;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class View extends SceneBuilder implements Time
{
    private static View instance = new View();
    private Text money;

    private View()
    {
        super(MainView.WIDTH, MainView.HEIGHT, Color.BLACK);
        getRoot().relocate(MainView.OFFSET_X, MainView.OFFSET_Y);
    }

    public static View getInstance()
    {
        return instance;
    }

    @Override
    protected void build()
    {
        childrenList.addAll(Background.getInstance());
//        childrenList.addAll(Background.getInstance(), MapView.getInstance(),
//                view.gameScene.warehouse.View.getInstance());
//        emptyWorkshopGraphic();
        wellGraphic();
        warehouseGraphic();
        moneyGraphic();
        truckGraphic();
        buildWorkshopGraphic();
        helicopterGraphic();
        gameMenuButton();
        childrenList.addAll(view.gameScene.truck.View.getInstance());
        childrenList.addAll(view.gameScene.workshop.View.getInstance());
        childrenList.addAll(GameMenu.getInstance());
        childrenList.addAll(view.settings.View.getInstance());
        SoundPlayer.getInstance().playBackground(Utility.getSound(SoundAddresses.DEFAULT_INGAME_MUSIC));
    }

    private void wellGraphic()
    {
        double XValue = MainView.WIDTH * 0.4;
        double YValue = MainView.HEIGHT / 9;

        ImageView wellImageView =
                Utility.getImageView(PictureAddresses.WELL_PICTURE_ROOT + Well.getInstance().getLevel() + ".png");
        wellImageView.relocate(XValue, YValue);
        wellImageView.setFitHeight(MainView.HEIGHT / 6);
        wellImageView.setFitWidth(MainView.WIDTH / 6);
        childrenList.addAll(wellImageView);

        SpriteAnimation wellSpriteAnimation = new SpriteAnimation(wellImageView, Duration.millis(1250), 16, 4,
                0, 0, (int) (wellImageView.getImage().getWidth() / 4),
                (int) wellImageView.getImage().getHeight() / 4);
        wellSpriteAnimation.stop();
        final boolean[] isWorking = {true};
        wellImageView.setOnMouseClicked(event -> {
            if (isWorking[0])
            {
                isWorking[0] = false;
                try
                {
                    MenuController.getInstance().refillWell();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                wellSpriteAnimation.setCycleCount(Well.getInstance().REFILL_TIME[Well.getInstance().getLevel()]);
                wellSpriteAnimation.playFromStart();
                wellSpriteAnimation.setOnFinished(event1 -> {
                    wellSpriteAnimation.stop();
                    isWorking[0] = true;
                });
            }
        });
    }

    private void warehouseGraphic()
    {
        Image warehouseImage = null;
        try
        {
            warehouseImage = new Image(new FileInputStream(
                    PictureAddresses.WAREHOUSE_PICTURE_ROOT + Warehouse.getInstance().getLevel() + ".png"));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(warehouseImage);
        imageView.setFitHeight(MainView.HEIGHT / 5);
        imageView.setFitWidth(MainView.WIDTH / 5);
        StackPane warehousePane = new StackPane();
        warehousePane.getChildren().addAll(imageView);
        warehousePane.relocate((MainView.WIDTH - warehouseImage.getWidth()) * 0.4,
                MainView.HEIGHT - 1.55 * warehouseImage.getHeight());
        childrenList.addAll(warehousePane);
        warehousePane.setOnMouseClicked(event -> openWarehouse());


    }

    private void moneyGraphic()
    {
        money = new Text(Integer.toString(InGameController.getInstance().getMoney()));
        money.setFill(Color.YELLOW);
        money.setFont(Font.font(30));
        StackPane pane = new StackPane();
        pane.getChildren().addAll(money);
        pane.setAlignment(Pos.CENTER);
        pane.relocate(MainView.WIDTH * 0.7, MainView.HEIGHT / 32);
        ImageView moneyImageview = new ImageView(Utility.getImage(PictureAddresses.GAME_BACKGROUND_ROOT + "money.png"));
        moneyImageview.relocate(MainView.WIDTH * 0.7, MainView.HEIGHT / 150);
        SpriteAnimation moneyAnimation = new SpriteAnimation(moneyImageview, Duration.millis(1200), 16, 4,
                0, 0, (int) (moneyImageview.getImage().getWidth() / 4),
                (int) (moneyImageview.getImage().getHeight() / 4));
        moneyAnimation.setCycleCount(Animation.INDEFINITE);
        moneyAnimation.play();
        childrenList.addAll(pane, moneyImageview);
    }

    private void truckGraphic()
    {

        ImageView imageView =
                new ImageView(Utility.getImage(PictureAddresses.TRUCK_PICTURE_ROOT + Truck.getInstance().getLevel() + ".png"));
        imageView.setFitWidth(MainView.WIDTH / 5);
        imageView.setFitHeight(MainView.HEIGHT / 5);
        StackPane truckPane = new StackPane();
        truckPane.getChildren().addAll(imageView);
        truckPane.relocate(MainView.WIDTH / 2 - imageView.getImage().getWidth() * 3.7,
                MainView.HEIGHT - imageView.getImage().getHeight() * 1.9);
        childrenList.addAll(truckPane);
        truckPane.setOnMouseClicked(event -> view.gameScene.truck.View.getInstance().openTruck());


    }

    private void buildWorkshopGraphic()
    {
        for (int i = 0; i < 6; i++)
        {
            int place = 0;
            double XValue = MainView.WIDTH, YValue = MainView.HEIGHT;
            switch (i)
            {
                case 0:
                    place = 1;
                    XValue *= 0.2;
                    YValue *= 0.3;
                    break;
                case 1:
                    place = 2;
                    XValue *= 0.2;
                    YValue *= 0.5;
                    break;
                case 2:
                    place = 3;
                    XValue *= 0.2;
                    YValue *= 0.65;
                    break;
                case 3:
                    place = 4;
                    XValue *= 0.85;
                    YValue *= 0.3;
                    break;
                case 4:
                    place = 5;
                    XValue *= 0.85;
                    YValue *= 0.5;
                    break;
                case 5:
                    place = 6;
                    XValue *= 0.85;
                    YValue *= 0.65;
                    break;

            }
            ImageView imageView = new ImageView(Utility.getImage(PictureAddresses.BUILD_WORKSHOP_ICON));
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.relocate(XValue, YValue);
            int finalPlace = place;
            imageView.setOnMouseClicked(event -> view.gameScene.workshop.View.getInstance().open(finalPlace));
            childrenList.addAll(imageView);


        }
    }

    private void helicopterGraphic()
    {

        ImageView imageView =
                new ImageView(Utility.getImage(PictureAddresses.HELICOPTER_PICTURE_ROOT + Helicopter.getInstance().getLevel() + ".png"));
        imageView.setFitWidth(MainView.WIDTH / 4.5);
        imageView.setFitHeight(MainView.HEIGHT / 4.5);
        StackPane helicopterPane = new StackPane();
        helicopterPane.getChildren().addAll(imageView);
        helicopterPane.relocate(MainView.WIDTH / 2 + imageView.getImage().getWidth() * 0.5,
                MainView.HEIGHT - imageView.getImage().getHeight() * 1.6);
        childrenList.addAll(helicopterPane);
        helicopterPane.setOnMouseClicked(event -> openHelicopter());
//        childrenList.addAll(view.gameScene.helicopter.View.getInstance());

    }

    private void gameMenuButton()
    {
        Utility.makeMenuButton(childrenList, -MainView.HEIGHT * 0.05, MainView.HEIGHT * 0.9,
                MainView.HEIGHT * 0.2, MainView.HEIGHT * 0.1
                , "MENU", event -> {
                    InGameController.getInstance().pauseGame();
                    GameMenu.getInstance().play();
                });

    }

    private void openWarehouse()
    {
        view.gameScene.warehouse.View.getInstance().UpdateInformation();
        view.gameScene.warehouse.View.getInstance().setVisible(true);

    }

    private void openHelicopter()
    {
//        view.gameScene.helicopter.View.getInstance().updateInformation();
//        view.gameScene.helicopter.View.getInstance().setVisible(true);
    }

    public void drawWorkshop(int place, String workshop)
    {//string hamun workshop.name hast
        switch (place)
        {
            case 1:
            {

                break;
            }
            case 2:
            {

                break;
            }
            case 3:
            {

                break;
            }
            case 4:
            {

                break;
            }
            case 5:
            {

                break;
            }
            case 6:
            {

                break;
            }
        }
    }

    @Override
    public void nextTurn()
    {
        getMoney();
        MapView.getInstance().nextTurn();
        Background.getInstance().nextTurn();
    }

    public void getMoney()
    {
        money.setText(Integer.toString(InGameController.getInstance().getMoney()));
    }

    public void closehelicopter()
    {
//        view.gameScene.helicopter.View.getInstance().setVisible(false);
    }

    public void closeTruck()
    {
        view.gameScene.truck.View.getInstance().setVisible(false);
    }

    public void openTruck()
    {
        view.gameScene.truck.View.getInstance().updateInformation();
        view.gameScene.truck.View.getInstance().setVisible(true);
    }

    public void closeWarehouse()
    {
        //childrenList.remove(View.getInstance());
        view.gameScene.warehouse.View.getInstance().setVisible(false);
    }

    public void showTruckPath()
    {
        ImageView truckView =
                new ImageView(Utility.getImage(PictureAddresses.TRUCK_MINI_PICTURE_ROOT + Truck.getInstance().getLevel() + "_mini.png"));
        truckView.setViewport(new Rectangle2D(0, 0, 48, 48));
        truckView.setFitWidth(MainView.WIDTH / 20);
        truckView.setFitHeight(MainView.HEIGHT / 20);
        truckView.relocate(MainView.WIDTH * 0.75, MainView.HEIGHT / 35);
        truckView.setScaleX(-1);
        childrenList.addAll(truckView);
        double end = MainView.WIDTH - MainView.WIDTH * 0.75 - 48;
        KeyValue xForGoing = new KeyValue(truckView.xProperty(), end);
        KeyFrame going = new KeyFrame(Duration.millis(2000), xForGoing);
        Timeline timeLineGoing = new Timeline(going);
        timeLineGoing.getKeyFrames().addAll(going);
        timeLineGoing.play();
        Animation animation = new SpriteAnimation(truckView, Duration.millis(2000), 2, 2, 0, 0,
                (int) (truckView.getImage().getWidth() / 2), (int) (truckView.getImage().getHeight()));
        animation.setCycleCount(1);
        animation.play();
        animation.setOnFinished(event -> {
            childrenList.removeAll(truckView);
            truckView.relocate(MainView.WIDTH - 48, MainView.HEIGHT / 35);
            truckView.setScaleX(1);
            childrenList.addAll(truckView);
            KeyValue xForGoing1 = new KeyValue(truckView.xProperty(), -MainView.WIDTH / 75);

            KeyFrame going1 = new KeyFrame(Duration.millis(2000), xForGoing1);
            Timeline timeLineGoing1 = new Timeline(going1);
            timeLineGoing1.getKeyFrames().addAll(going1);
            timeLineGoing1.play();

            animation.setCycleCount(1);
            animation.play();
            animation.setOnFinished(event1 -> {
                childrenList.removeAll(truckView);
            });
        });

    }

    public void showHelicopterPath()
    {
        ImageView helicopterkView =
                new ImageView(Utility.getImage(PictureAddresses.HELICOPTER_MINI_PICTURE_ROOT + Truck.getInstance().getLevel() + "_mini.png"));
        helicopterkView.setViewport(new Rectangle2D(0, 0, 48, 48));
        helicopterkView.setFitWidth(MainView.WIDTH / 20);
        helicopterkView.setFitHeight(MainView.HEIGHT / 20);
        helicopterkView.relocate(MainView.WIDTH * 0.75, MainView.HEIGHT / 150);
        helicopterkView.setScaleX(1);
        childrenList.addAll(helicopterkView);
        double end = MainView.WIDTH - MainView.WIDTH * 0.75 - 48;
        KeyValue xForGoing = new KeyValue(helicopterkView.xProperty(), end);
        KeyFrame going = new KeyFrame(Duration.millis(2000), xForGoing);
        Timeline timeLineGoing = new Timeline(going);
        timeLineGoing.getKeyFrames().addAll(going);
        timeLineGoing.play();
        Animation animation = new SpriteAnimation(helicopterkView, Duration.millis(2000), 6, 3, 0, 0,
                (int) (helicopterkView.getImage().getWidth() / 3), (int) (helicopterkView.getImage().getHeight() / 2));
        animation.setCycleCount(1);
        animation.play();
        animation.setOnFinished(event -> {
            childrenList.removeAll(helicopterkView);
            helicopterkView.relocate(MainView.WIDTH - 48, MainView.HEIGHT / 150);
            helicopterkView.setScaleX(-1);
            childrenList.addAll(helicopterkView);
            KeyValue xForGoing1 = new KeyValue(helicopterkView.xProperty(), -MainView.WIDTH / 75);

            KeyFrame going1 = new KeyFrame(Duration.millis(2000), xForGoing1);
            Timeline timeLineGoing1 = new Timeline(going1);
            timeLineGoing1.getKeyFrames().addAll(going1);
            timeLineGoing1.play();

            animation.setCycleCount(1);
            animation.play();
            animation.setOnFinished(event1 -> {
                childrenList.removeAll(helicopterkView);
            });
        });

    }

}
