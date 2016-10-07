

abstract class Action implements IAction
{	
	Action(Person who_does)
	{
		by_whom = who_does;
		time_to_completion = 5;
	}
	
	
	protected void cheaty_copy_action(Action copied_from, Person new_actor)
	{
		time_to_completion = copied_from.time_to_completion;
		by_whom = new_actor;
	}
	
	public boolean can_be_done(){return true;}
	
	public String Get_Description(){return description;}
	
	public String description;
	
	public int get_time_to_completion()
	{
		return time_to_completion;
	}
	
	protected int time_to_completion;
	protected Person by_whom;
}


interface IAction
{
	public boolean can_be_done();
	public void What_Happens();
	public String Get_Description();
	
	public int get_time_to_completion();
	public Action copy_Action(Person to_whom);
	
}

interface NPC_Action extends IAction
{	
	abstract int how_likely();
	
	public int liklihood=1;
}