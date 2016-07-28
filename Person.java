
public abstract class Person
{
	abstract public void add_item(item new_item);
	
	abstract public Person get_hate();
	
	public String get_name()
	{
		return name;
	}
	
	public Location getCurrent_location()
	{
		return current_location;
	}
	
	abstract public boolean has_item(item what);
	abstract public void gain(item what, int how_much);	
	abstract public void injure(int x);
	abstract protected void die();
	abstract int get_fight_power();
	
	void LoseFight(int how_much)
	{
		injure(how_much);
	}
	
	protected int current_hp;
	protected Location current_location;
	protected String name;
	protected int total_hp;
	
}


///////////////////////////////
abstract class Action
{	
	Action(){}
	
	public boolean can_be_done(){return true;}
	
	public String Get_Description(){return description;}
	abstract public void What_Happens();
	public String description;
	
	//TODO: add support for long/short actions
	//private double time_to_completion
}
