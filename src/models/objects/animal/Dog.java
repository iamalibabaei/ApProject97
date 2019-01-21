package models.objects.animal;

import models.Map;
import models.objects.Entity;
import models.objects.Point;

// todo dog upgrade

public class Dog extends Animal
{
    public Dog(Point point, Animal.Type type)
    {
        super(point, type);
    }

    @Override
    public void collide(Entity entity)
    {
        if (entity instanceof WildAnimal)
        {
            entity.die();
        }
    }

    @Override
    public void nextTurn()
    {
        super.nextTurn();
    }

    @Override
    public void setTarget()
    {
        target = null;

        for (Animal animal : map.getAnimals())
        {
            if (animal instanceof WildAnimal)
            {
                target.setX(animal.getCoordinates().getX());
                target.setY(animal.getCoordinates().getY());
            }
        }
    }

}
