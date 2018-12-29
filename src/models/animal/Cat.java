package models.animal;

import models.Entity;
import models.Item;
import models.Warehouse;
import models.exceptions.AlreadyAtMaxLevelException;
import models.interfaces.Buyable;
import models.interfaces.Upgradable;
import models.map.Cell;
import models.map.Map;
import java.util.HashMap;

public class Cat extends Animal implements Buyable, Upgradable
{
    private static final int BUY_COST = 2500;
    private static final int MAX_LEVEL = 2, UPGRADE_COST = 200;
    private static int level;
    private HashMap<Item.Type, Integer> items = new HashMap();
    private Warehouse warehouse = new Warehouse();

    public Cat(int x, int y, Map map)
    {
        super(x, y, map);
    }

    @Override
    public void setTarget()
    {
        super.target = null;

        if (level == 1){
            outer : for (Cell[] cells: super.map.getCells()) {
                for (Cell cell: cells) {
                    for (Entity entity : cell.getEntities()) {
                        if (entity instanceof Item){
                            super.target = entity;
                            break outer;
                        }
                    }
                }
            }
        }
        else {
            int dist = 1000;
            for (Cell[] cells: super.map.getCells()) {
                for (Cell cell: cells) {
                    for (Entity entity : cell.getEntities()) {
                        if (entity instanceof Item){
                            int dist1 = Math.abs(this.getX() - this.target.getX()) + Math.abs(this.getY() - this.target.getY());
                            if (dist1 < dist) {
                                dist = dist1;
                                super.target = entity;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void collide(Entity entity)
    {
        for (Entity entity1 : super.map.getCell(entity.getX(), entity.getY()).getEntities()){
            if (entity1 instanceof Item){
                items.put(entity1, 1);
            }
        }
        warehouse.store(items);
        setTarget();
    }

    @Override
    public int getBuyCost()
    {
        return BUY_COST;
    }

    @Override
    public void upgrade() throws AlreadyAtMaxLevelException
    {
        if (level == MAX_LEVEL)
            throw new AlreadyAtMaxLevelException();
        level++;
    }
    @Override
    public int getUpgradeCost() throws AlreadyAtMaxLevelException
    {
        if (level == MAX_LEVEL)
            throw new AlreadyAtMaxLevelException();
        return UPGRADE_COST;
    }

}
