public class Parser {

	public String[] evaluateInput(String line){
	// if the commands starts with create
	String[] commands = new String[10];
	commands = line.split(" ");
	
	//test display
	for(int i=0 ;i< commands.length; i++){
		System.out.println(commands[i]);
	}
	switch(commands[0])
	{ 
		case 'create':{
			System.out.println("Create selected");
			create(commands);
			break;}
		case 'display':{
			System.out.println("Display selected");
			display(commands);
			break;
		}
		case 'delete':{
			System.out.println("Delete selected");
			delete(commands);
			break;
		}
		case 'edit':{
			System.out.println("Edit selected"); 
			String[] editSplit = new String[2];
			editSplit = commands[0].split('-');
			//split to the different types
			break;
			
		}
		default:{
			System.out.println("ERROR");
			break;
		}
	}
	}
}
