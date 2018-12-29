
package models.transportation;

import models.exceptions.AlreadyAtMaxLevelException;
import models.exceptions.IsWorkingException;
import models.Item;
import models.map.Map;


public class Buyer extends Transporter
{
    private Map map;
    public Buyer(Map map) {
        this.capacity = 25;
        this.speed = 12;
        this.level = 0;
        this.map = map;
    }

    @Override
    public void go() throws IsWorkingException
    {
        if (isWorking) {
            throw new IsWorkingException();
        }
        isWorking = true;
        arriveToFarm = speed;
    }

    @Override
    public void turn() {
        arriveToFarm --;
        if (arriveToFarm == 0) {
            for (Item.Type itemType : list.keySet()) {
                for (int i = 0; i < list.get(itemType); i++) {
                    int x = (int)(Math.random() * 20), y = (int)(Math.random() * 20);
                    Item item = new Item(x, y, itemType);
                    map.addToMap(item);
                }
            }
            isWorking = false;
            list.clear();
        }
    }

    @Override
    public void upgrade() throws AlreadyAtMaxLevelException
    {
        if (level == 3) {
            throw new AlreadyAtMaxLevelException();
        }
        this.level ++;
        this.speed = this.speed - 3;
        this.capacity =(int) (this.capacity * 1.5);
    }

    @Override
    public int getUpgradeCost() throws AlreadyAtMaxLevelException {
        int[] costList = {400, 800, 1600};
        if (level == 3) {
            throw new AlreadyAtMaxLevelException();
        }
        return costList[level];
    }
}
