

abstract class Action implements IAction
{	
	Action(Person who_does){by_whom = who_does;}
	
	public boolean can_be_done(){return true;}
	
	public String Get_Description(){return description;}
	
	public String description;
	
	//TODO: add support for long/short actions
	//private double time_to_completion
	protected Person by_whom;
}


interface IAction
{
	public boolean can_be_done();
	abstract public void What_Happens();
	public String Get_Description();
}

interface NPC_Action extends IAction
{	
	abstract int how_likely();
	
	public int liklihood=1;
}