
public abstract class Person
{
	abstract public void injure(int x);
	
	abstract public void add_item(item new_item);
	
	protected int total_hp;
	protected int current_hp;

}


///////////////////////////////
abstract class Action
{	
Action(){}

public String Get_Description(){return description;}

public boolean can_be_done(){return true;}
public void What_Happens(){}
public String description;

//TODO: add support for long/short actions
//private double time_to_completion
}
