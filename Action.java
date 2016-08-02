

abstract class Action implements IAction
{	
	Action(){}
	
	public boolean can_be_done(Person by_whom){return true;}
	
	public String Get_Description(){return description;}
	
	public String description;
	
	//TODO: add support for long/short actions
	//private double time_to_completion
}


interface IAction
{
	public boolean can_be_done(Person by_whom);
	abstract public void What_Happens();
	public String Get_Description();
}

interface NPC_Action extends IAction
{	
	abstract int how_likely();
	
	public int liklihood=1;
}