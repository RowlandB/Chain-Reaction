import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

//uses JFrame + Buttons
class Frame_Button_IO_Object extends IO_Object
{
	Frame_Button_IO_Object()
	{
		JFrame frame = new JFrame("Chain Reaction");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new GridBagLayout());
		
		TextArea header = new TextArea();
		header.setPreferredSize(new Dimension(100,200));
		header.setEditable(false);
		header.setText("test string");
		
			GridBagConstraints GBC = new GridBagConstraints();
			GBC.fill = GridBagConstraints.BOTH;
			
			GBC.weightx = 5.0;
			GBC.weighty = 5.0;
			GBC.gridwidth = GridBagConstraints.REMAINDER;
		
		frame.getContentPane().add(header, GBC);
		
		Container button_spot = Box.createVerticalBox();
		button_spot.setPreferredSize(new Dimension(600, 300));
//		button_spot.add(Box.createVerticalGlue());
		
			GBC.weightx = 10.0;
			GBC.weighty = 10.0;
			GBC.gridwidth = GridBagConstraints.REMAINDER;

		frame.getContentPane().add(button_spot, GBC);
		
		//TODO: push this to another function so it doesn't clutter things up
		TextField input = new TextField();
		input.setPreferredSize(new Dimension(400,50));
		input.setEditable(true);
		
		GBC.weightx = 1.0;
		GBC.weighty = 1.0;
		frame.getContentPane().add(input, GBC);
		
		Input_Field = input;
		
		JButton OK = new JButton("OK");
		OK.setPreferredSize(new Dimension(60,50));
		OK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(!Input_Field.getText().equals(""))
				{
					ready_for_input=true;
				}
			}});
		
		frame.add(OK);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		header_text = header;
		button_holder = button_spot;
		button_list = new Vector<JButton>();
		previous_output = "";
	}
	
	public String Input_String()
	{
		ready_for_input=false;
		while(!ready_for_input)
		{
			try {
			    Thread.sleep(200);                 //200 milliseconds
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		if(button_list.size()==0)
		{
			return Input_Field.getText();
		}
		else
		{
			Delete_All_Buttons();
			previous_output = "";
			
			//TODO change all this to INTs
			return Integer.toString(future_return);
		}
	}
	
	public void Output_Batch()
	{
		button_holder.revalidate();
		button_holder.repaint();
	}
	
	public void Output_String(String output)
	{
		previous_output = previous_output + output + "\n";
		
		//TODO shouldn't be called, assume it's a header
		header_text.setText(previous_output);
		
		//header_text.setCaretPosition(output.length());
		log = log + output;
	}
	
	public void Partial_List_Output_String(int which, String Output)
	{
		Partial_List_Output_String(which, Output, false);
	}
	
	public void Partial_List_Output_String(int which, String Output, boolean red_text)
	{
		JButton potential_option = new JButton();
		potential_option.setText(Integer.toString(which) + ") " + Output);
		potential_option.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				future_return = which;
				ready_for_input = true;
			}});
		
		if(red_text)
		{
			potential_option.setForeground(Color.RED);
		}
		
		button_holder.add(potential_option);
		button_list.add(potential_option);
		
		log = log + Integer.toString(which) + ") " + Output;
	}
	
	private void Delete_All_Buttons()
	{
		for(int x=0; x<button_list.size(); x++)
		{
			button_holder.remove(button_list.get(x));
		}
		button_list.removeAllElements();
	}
	
	Vector<JButton> button_list;
	boolean ready_for_input;	
	private String log;
	TextArea header_text;
	Container button_holder;
	int future_return;
	String previous_output;
	TextField Input_Field;
}

//uses JFrames
class Frame_IO_Object extends IO_Object
{
	Frame_IO_Object()
	{
		JFrame frame = new JFrame("Chain Reaction");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new GridBagLayout());
				
		TextArea display = new TextArea();
		display.setPreferredSize(new Dimension(600,300));
		display.setEditable(false);
		display.setText("test string");
		
			GridBagConstraints GBC = new GridBagConstraints();
			GBC.fill = GridBagConstraints.BOTH;
			
			GBC.weightx = 5.0;
			GBC.weighty = 5.0;
			GBC.gridwidth = GridBagConstraints.REMAINDER;


		frame.getContentPane().add(display, GBC);
		
		
		TextField input = new TextField();
		input.setPreferredSize(new Dimension(400,50));
		input.setEditable(true);
		
		GBC.weightx = 1.0;
		GBC.weighty = 1.0;
		frame.getContentPane().add(input, GBC);
		
		
		JButton button = new JButton("OK");
		button.setPreferredSize(new Dimension(60,50));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				new_input_text=input_text.getText();
				if(!new_input_text.equals(""))
				{
					ready_for_input=true;
				}
			}});
		
		GridBagConstraints GBC2 = new GridBagConstraints();
		GBC2.anchor = GridBagConstraints.EAST;
		GBC2.gridwidth = GridBagConstraints.RELATIVE;
		frame.getContentPane().add(button, GBC2);
		frame.getRootPane().setDefaultButton(button);
		
		
		JButton clear_button = new JButton("Clear");
		clear_button.setPreferredSize(new Dimension(70,50));
		clear_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{				
				output_text.setText(last_text);
				current_output_text="";
			}});
		
		GBC2.gridwidth = GridBagConstraints.REMAINDER;
		frame.add(clear_button, GBC2);
	
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		last_text="";
		buffer_text="";
		current_output_text="";
		output_text=display;
		input_text=input;
		window=frame;
		
		input_text.requestFocusInWindow();
	}
	
	public String Input_String()
	{
		ready_for_input=false;
		while(!ready_for_input)
		{
			try {
			    Thread.sleep(200);                 //200 milliseconds
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		input_text.setText("");
		return new_input_text;
	}
	
	public void Output_Batch()
	{
		last_text = buffer_text;
		buffer_text="";
		current_output_text = current_output_text + last_text;
		output_text.setText(current_output_text);
		
		output_text.setCaretPosition(current_output_text.length());
	}
	
	public void Output_String(String Output)
	{
		buffer_text = buffer_text + Output  + "\r\n";
	}
	
	boolean ready_for_input;
	String buffer_text;
	String current_output_text;
	String last_text;
	String new_input_text;
	TextField input_text;
	TextArea output_text;
	JFrame window;
}

//////////////////////////////
//Exactly what it says on the tin: handles input and output
abstract class IO_Object
{
	public String Input_String(){
		return "balls. This shouldn't have been called";
	}
	
	public void Output_Batch(){}
	
	public void Output_String(String output){}
	
	public void Partial_List_Output_String(int which, String Output)
	{
		Output_String(Integer.toString(which) + ") " + Output);
	}

	public void Partial_List_Output_String(int which, String Output, boolean b)
	{
		Output_String(Integer.toString(which) + ") " + Output);
	}
}

//uses System IO
class System_IO_Object extends IO_Object
{
	System_IO_Object(){}
	
	public String Input_String()
	{
		Scanner keyboard = new Scanner(System.in);
		String the_input = keyboard.nextLine();
		return the_input;
	}
	
	public void Output_String(String output)
	{
		System.out.println(output);
	}
}