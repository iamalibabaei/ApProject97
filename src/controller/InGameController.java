package controller;

import models.Map;
import models.exceptions.Messages;
import models.buildings.Warehouse;
import models.buildings.Well;
import models.buildings.Workshop;
import models.exceptions.*;
import models.interfaces.Time;
import models.misc.Mission;
import models.objects.Item;
import models.objects.Point;
import models.objects.animals.Animal;
import models.objects.animals.Cat;
import models.objects.animals.Dog;
import models.objects.animals.DomesticAnimal;
import models.transportation.Helicopter;
import models.transportation.Truck;
import view.gameScene.View;

import javax.naming.InsufficientResourcesException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// todo you have to buy helicopter in the beginning
// todo multiple trucks and helicopters

public class InGameController implements Time
{
    public static final double COLLISION_RADIUS = 2.0;
    private static InGameController instance = new InGameController();
    private Integer money;
    private Map map;
    private Warehouse warehouse;
    private Well well;
    private Truck truck;
    private Helicopter helicopter;
    private List<Workshop> workshops;
    private Mission mission;
    private ArrayList<String> availableWorkshops;
    private final int FPS = 60, SECOND_PER_FRAME = 1000 / FPS;

    public void moneyDeposit(Integer money) {
        this.money += money;
    }


    private InGameController()
    {
        availableWorkshops = new ArrayList<>();
        map = Map.getInstance();
        warehouse = Warehouse.getInstance();
        well = Well.getInstance();
        workshops = new ArrayList<>();
        truck = Truck.getInstance();
        helicopter = Helicopter.getInstance();
        money = 0;
    }

    public static InGameController getInstance()
    {
        return instance;
    }

    public Integer getMoney()
    {
        return money;
    }

    public void buyAnimal(String name) throws InvalidArgumentException, InsufficientResourcesException
    {
        for (Animal.Type type : Animal.Type.values())
        {
            if (name.equals(type.toString().toLowerCase()))
            {
                if (money < type.BUY_COST)
                {
                    throw new InsufficientResourcesException();
                }
                map.addAnimal(type);
                withdrawMoney(type.BUY_COST);
                return;
            }
        }
        throw new InvalidArgumentException();
    }

    public void withdrawMoney(int cost) {
        if (money < cost)
        {
            throw new AssertionError();
        }
        money -= cost;
        View.getInstance().getMoney();

    }

    public void pickUp(Point point) throws IOException
    {
        List<Item> nearbyItems = map.getNearbyItems(point);
        List<Item> storedItems = warehouse.store(nearbyItems);
        map.removeItems(storedItems);
    }

    public void store(List<Item> items) throws IOException
    {
        List<Item> storedItems = warehouse.store(items);
        map.removeItems(storedItems);
    }

    public void cage(Point point)
    {
        map.cage(point);
    }

    public void startWorkshop(String workshopName) throws IOException
    {
        for (Workshop workshop : workshops)
        {
            if (workshop.name.equals(workshopName))
            {
                int factor = warehouse.moveToWorkshop(workshop.inputs, workshop.getMaxProductionFactor());
                workshop.startWorking(factor);
            }
        }
    }

    public void upgrade(String parameter) throws IOException
    {
        // todo
    }

    public void refillWell() throws Exception
    {
        int cost = Well.REFILL_COST[well.getLevel()];
        if (money < cost)
        {
            throw new IOException(Messages.NOT_ENOUGH_MONEY);
        }
        withdrawMoney(cost);
        well.issueRefill();
    }

    public void upgradeWell() throws Exception
    {
        int cost = well.getUpgradeCost();
        if (money < cost)
        {
            throw new IOException(Messages.NOT_ENOUGH_MONEY);
        }
        withdrawMoney(cost);
        well.upgrade();
    }

    public void plant(Point point) throws Exception
    {
        well.extractWater();
        map.plant(point);
    }

    public void sendHelicopter() throws IOException
    {
        int cost = helicopter.computePrice();
        if (money < cost)
        {
            throw new IOException(Messages.NOT_ENOUGH_MONEY);
        }
        withdrawMoney(cost);
        helicopter.go();
    }

    public void addWorkshop(Workshop workshop, int place)
    {
        workshops.add(workshop);
        View.getInstance().drawWorkshop(place, workshop.name);
    }

    public void clearStash(String transporterName) throws InvalidArgumentException
    {
        if (transporterName.equals(helicopter.getName()))
        {
            helicopter.clearStash();
        } else if (transporterName.equals(truck.getName()))
        {
            truck.clearStash();
        } else
            throw new InvalidArgumentException();
    }

    public void removeFromStash(String transporterName, String itemName) throws InvalidArgumentException
    {
        Item.Type item = Item.Type.NONE;
        for (Item.Type type : Item.Type.values())
        {
            if (type.toString().equals(itemName))
            {
                item = type;
                break;
            }
        }
        if (item == Item.Type.NONE)
            throw new InvalidArgumentException();
        if (helicopter.getName().equals(transporterName))
        {
            helicopter.removeFromList(item, 1);
        } else if (truck.getName().equals(transporterName))
        {
            truck.removeFromList(item, 1);
        } else
        {
            throw new InvalidArgumentException();
        }
    }

    public void sendTruck()
    {
        truck.go();
        warehouse.remove(truck.getList());
    }

    public void addToStash(String transporterName, String itemName, int count) throws IOException
    {
        Item.Type item = Item.Type.NONE;
        for (Item.Type type : Item.Type.values())
        {
            if (type.toString().equals(itemName))
            {
                item = type;
                break;
            }
        }
        if (item == Item.Type.NONE)
            throw new RuntimeException();
        if (helicopter.getName().equals(transporterName))
        {
            helicopter.addToList(item, count);
        } else if (truck.getName().equals(transporterName))
        {
            truck.addToList(item, count);
        } else
        {
            throw new RuntimeException();
        }
    }

    @Override
    public void nextTurn()
    {
        map.nextTurn();
        truck.nextTurn();
        helicopter.nextTurn();
        for (Workshop workshop : workshops)
        {
            workshop.nextTurn();
        }
    }

    public void loadMission(){
        moneyDeposit(mission.getMoneyAtBeginning());
        for (Animal.Type animal : mission.getAnimalAtBeginning().keySet()) {
            for (int i = 0; i < mission.getAnimalAtBeginning().get(animal); i++) {
                //TODO add animal to map
            }
        }
        InGameController.getInstance().moneyDeposit(mission.getMoneyAtBeginning());
    }

    public void startGame(Mission mission) {
        this.mission = mission;

        loadMission();
        //TODO loop for next turn
    }

    private boolean isAccomplished()
    {
        boolean hasDog = false, hasCat = false;
        int gameMoney = getMoney();
        Map map = Map.getInstance();
        HashMap<DomesticAnimal.Type, Integer> animalCurrentState = new HashMap<>();
        HashMap<Item.Type, Integer> itemCurrentState = new HashMap<>();

        for (Item item : map.getItems())
        {
            itemCurrentState.put(item.type, itemCurrentState.getOrDefault(item.type, 0) + 1);
        }

        for (Animal animal : map.getAnimals())
        {
            if (animal instanceof Dog)
            {
                hasDog = true;
            } else if (animal instanceof Cat)
            {
                hasCat = true;
            } else if (animal instanceof DomesticAnimal)
            {
                animalCurrentState.put(((DomesticAnimal) animal).type
                        , animalCurrentState.getOrDefault(((DomesticAnimal) animal).type, 0) + 1);
            }

        }
        return checkAccomplishment(gameMoney, animalCurrentState, itemCurrentState, hasCat, hasDog);
    }

    private boolean checkAccomplishment(int money, HashMap<DomesticAnimal.Type, Integer> animalCurrentState,
                                        HashMap<Item.Type, Integer> ItemCurrentState, boolean hasCat, boolean hasDog)
    {
        if (mission.getMoneyObjective() > money)
        {
            return false;
        }
        for (DomesticAnimal.Type type : animalCurrentState.keySet())
        {
            if (mission.getAnimalObjectives().containsKey(type))
            {
                if (mission.getAnimalObjectives().get(type) > animalCurrentState.get(type))
                {
                    return false;
                }
            }
        }

        for (Item.Type type : ItemCurrentState.keySet())
        {
            if (mission.getItemObjective().containsKey(type))
            {
                if (mission.getItemObjective().get(type) > ItemCurrentState.get(type))
                {
                    return false;
                }
            }
        }
        if (mission.isDog() && !hasDog)
        {
            return false;
        }
        return !mission.isCat() || hasCat;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public ArrayList<String> getAvailableWorkshops() {
        return availableWorkshops;
    }
}
