package view.gameScene;

import controller.MenuController;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.Map;
import models.Viewable;
import models.interfaces.Time;
import models.objects.Entity;
import models.objects.Grass;
import models.objects.Item;
import models.objects.animals.Animal;
import view.MainView;
import view.utility.SpriteAnimation;

import java.util.ArrayList;

public class MapView extends Pane implements Time {
    private static MapView instance = new MapView();
    public static final double WIDTH_BASE = 17.0, HEIGHT_BASE = 12.76;
    private ArrayList<Text> entitiesInMap;
    public static MapView getInstance() {
        return instance;
    }

    private MapView() {
        entitiesInMap = new ArrayList<>();
        this.setWidth(MainView.WIDTH * 0.5);
        this.setHeight(MainView.HEIGHT * 0.5);
        this.relocate(MainView.HEIGHT * 0.3, MainView.HEIGHT * 0.3);




        build();
        setOnMouseClicked(event -> {
            double x = event.getX() / WIDTH_BASE, y = event.getY() / HEIGHT_BASE;
            if (0 <= x && x < 30.0 && y < 30.0 && 0 <= y) {
                System.out.println(x+"   "+ y);
                MenuController.getInstance().click((event.getX()) / WIDTH_BASE, event.getY() / HEIGHT_BASE);
            }
        });
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    private void build() {
        // shows corner of map
        Rectangle rectangle1 = new Rectangle(10, 10);
        rectangle1.setFill(Color.BLUE);
        rectangle1.relocate(0,0);

        Rectangle rectangle2 = new Rectangle(10, 10);
        rectangle2.setFill(Color.BLUE);
        rectangle2.relocate(this.getWidth() - 10,this.getHeight()- 10);

        Rectangle rectangle3 = new Rectangle(10, 10);
        rectangle3.setFill(Color.BLUE);
        rectangle3.relocate(0,this.getHeight()- 10);

        Rectangle rectangle4 = new Rectangle(10, 10);
        rectangle4.setFill(Color.BLUE);
        rectangle4.relocate(this.getWidth() - 10,0);
        rectangle1.setVisible(false);
        rectangle2.setVisible(false);
        rectangle3.setVisible(false);
        rectangle4.setVisible(false);

        getChildren().addAll(rectangle1, rectangle2, rectangle3, rectangle4);
    }




    @Override
    public void nextTurn() {
        getChildren().clear();
        this.setWidth(MainView.WIDTH * 0.5);
        this.setHeight(MainView.HEIGHT * 0.5);
        this.relocate(MainView.HEIGHT * 0.3, MainView.HEIGHT * 0.3);

        build();

        setOnMouseClicked(event -> {
            double x = event.getX() / WIDTH_BASE, y = event.getY() / HEIGHT_BASE;
            if (0 <= x && x < 30.0 && y < 30.0 && 0 <= y) {
                System.out.println(x+"   "+ y);
                MenuController.getInstance().click((event.getX()) / WIDTH_BASE, event.getY() / HEIGHT_BASE);
            }
        });


        for (Animal animal: Map.getInstance().getAnimals()) {
            double x = animal.getCoordinates().getX() * WIDTH_BASE - MainView.HEIGHT * 0.05;
            double y = animal.getCoordinates().getY() * HEIGHT_BASE - MainView.HEIGHT * 0.05;
            int coulmn = 0, row = 0;
            Animal.Type type = animal.type;
            if (type == Animal.Type.HEN) {
                coulmn = 5;
                row = 5;
            } else if (type == Animal.Type.SHEEP) {
                Viewable.stateKind state =animal.getState();
                coulmn = 4;
                row = 6;
                if(state == Viewable.stateKind.DIE || state == Viewable.stateKind.DOWN ||
                        state == Viewable.stateKind.DOWN_LEFT || state == Viewable.stateKind.DOWN_RIGHT ||
                        state == Viewable.stateKind.EAT){
                    coulmn = 6;
                    row = 4;
                }
            }else if (type == Animal.Type.LION) {
                coulmn = 6;
                row = 4;
            } else if (type == Animal.Type.COW) {
                coulmn = 3;
                row = 8;
            } else if (type == Animal.Type.DOG) {
                coulmn = 6;
                row = 4;
            } else if (type == Animal.Type.CAT) {
                coulmn = 6;
                row = 4;
                if (animal.getState() == Viewable.stateKind.LEFT || animal.getState() == Viewable.stateKind.RIGHT ) {
                    coulmn = 4;
                    row = 6;
                }
            }
            animal.updateImageView();
//            System.out.println("animal " + ((Animal) entity).type + " Xtarget " + );
            ImageView imageView = animal.getImageView();
//            imageView.setViewport(new Rectangle2D(0, 0, imageView.getImage().getWidth() / coulmn,
//                    imageView.getImage().getHeight() / row));
            if (animal.getImageView() == null) {
                animal.setState(Viewable.stateKind.EAT);
                animal.updateImageView();
                imageView = animal.getImageView();
            }
            imageView.relocate(x, y);
            getChildren().addAll(animal.getImageView());
            SpriteAnimation s = new SpriteAnimation(animal.getImageView(), Duration.millis(1000), row * coulmn,
                    coulmn, 0, 0,(int)(animal.getImageView().getImage().getWidth() / coulmn), (int)(animal.getImageView().getImage().getHeight() / row));


            s.stop();

            s.setCycleCount(-1);
            s.play();


        }

        for (Item item : Map.getInstance().getItems()) {
            double x = item.getCoordinates().getX() * WIDTH_BASE - MainView.HEIGHT * 0.05;
            double y = item.getCoordinates().getY() * HEIGHT_BASE - MainView.HEIGHT * 0.05;

            ImageView imageView = item.getImageView();
            imageView.relocate(x, y);
            this.getChildren().addAll(imageView);

        }






        for (Grass grass : Map.getInstance().getGrasses()) {
            double x = grass.getCoordinates().getX() * WIDTH_BASE - MainView.HEIGHT * 0.05;
            double y = grass.getCoordinates().getY() * HEIGHT_BASE - MainView.HEIGHT * 0.05;
            ImageView imageView = grass.getImageView();
            imageView.relocate(x, y);

            this.getChildren().addAll(imageView);


        }

    }

    public void addEntity(Entity entity) {
        double x = entity.getCoordinates().getX() * WIDTH_BASE - MainView.HEIGHT * 0.05;
        double y = entity.getCoordinates().getY() * HEIGHT_BASE - MainView.HEIGHT * 0.05;
        if (entity instanceof Grass) {
            ImageView imageView = entity.getImageView();
            imageView.setViewport(new Rectangle2D(0, 0, imageView.getImage().getWidth() / 4,
                    imageView.getImage().getHeight() / 4));
            imageView.relocate(x, y);

            this.getChildren().addAll(imageView);


        }

        if (entity instanceof Animal) {
            int coulmn = 0, row = 0;
            Animal.Type type = ((Animal) entity).type;
            if (type == Animal.Type.HEN) {
                coulmn = 5;
                row = 5;
            } else if (type == Animal.Type.SHEEP) {
                coulmn = 5;
                row = 5;
            }else if (type == Animal.Type.LION) {
                coulmn = 6;
                row = 4;
            } else if (type == Animal.Type.COW) {
                coulmn = 3;
                row = 8;
            } else if (type == Animal.Type.DOG) {
                coulmn = 6;
                row = 4;
            } else if (type == Animal.Type.CAT) {
                coulmn = 6;
                row = 4;
                if (entity.getState() == Viewable.stateKind.LEFT) {
                    coulmn = 4;
                    row = 6;
                }
            }
            ImageView imageView = entity.getImageView();
//            imageView.setViewport(new Rectangle2D(0, 0, imageView.getImage().getWidth() / coulmn,
//                    imageView.getImage().getHeight() / row));
            imageView.relocate(x, y);
            getChildren().addAll(entity.getImageView());
            SpriteAnimation s = new SpriteAnimation(entity.getImageView(), Duration.millis(1000), row * coulmn,
                    coulmn, 0, 0,(int)(entity.getImageView().getImage().getWidth() / coulmn), (int)(entity.getImageView().getImage().getHeight() / row));

            System.out.println("before stop");
            s.stop();
            System.out.println("before play");
            s.setCycleCount(100000);
            s.play();
            System.out.println("after play");
        }

        if (entity instanceof Item) {
            ImageView imageView = entity.getImageView();
            imageView.relocate(x, y);
            this.getChildren().addAll(imageView);
        }

    }
}
