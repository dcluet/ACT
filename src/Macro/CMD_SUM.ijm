//A.C.T. 2014/02/13

//Shortcut for the ACT GUI and main program
macro "A.C.T."{
	
	runMacro(getDirectory("macros")+File.separator()+"ACT_Batch"+File.separator()+"ACT_Table_CommandLine_creation.txt");	//Launch the GUI of ACT for the pre-treatment of the movies 
}


//Shortcut to launch a set of manually created command lines in batch-mode, skipping the GUI.
//The command lines can have different resolutions, extension,...
macro "A.C.T. Command-lines LAUNCHER"{


//CeCill license window	
PathImage = getDirectory("macros")+File.separator()+"ACT_Batch"+File.separator+"ACT.jpg";
open(PathImage);
T=getTitle;
waitForUser("Copyright CNRS 2013\n \nCLUET David      david.cluet@ens-lyon.fr\nSTEBE Pierre Nicolas   pierre.stebe@ens-lyon.fr\nSPICHTY Martin   spichty.martin@ens-lyon.fr\nDELATTRE Marie   marie.delattre@ens-lyon.fr\n \nThis software is a computer program whose purpose is to automatically track centrosomes\nin DIC movies.\n \nThis software is governed by the CeCILL license under French law and abiding by the rules\nof distribution of free software. You can use, modify and/or redistribute the software\nunder the terms of the CeCILL license as circulated by CEA, CNRS and INRIA at the following URL\nhttp://www.cecill.info/index.en.html.\n \nAs a counterpart to the access to the source code and  rights to copy, modify and redistribute\ngranted by the license, users are provided only with a limited warranty  and the software's author,\nthe holder of the economic rights, and the successive licensors have only limited liability.\n \nIn this respect, the user's attention is drawn to the risks associated with loading, using, modifying\nand/or developing or reproducing the software by the user in light of its specific status of free\nsoftware, that may mean  that it is complicated to manipulate, and that also therefore means  that\nit is reserved for developers  and  experienced professionals having in-depth computer knowledge. Users\nare therefore encouraged to load and test the software's suitability as regards their requirements\nin conditions enabling the security of their systems and/or data to be ensured and, more generally,\nto use and operate it in the same conditions as regards security.\n \nThe fact that you are presently reading this means that you have had knowledge of the CeCILL license\nand that you accept its terms.\n");
selectWindow(T);
close();

//Dialog interface. 
//The user specify the path of the txt file containing the command lines.
P = File.openDialog("");
	Listing=split(File.openAsString(P),"\n"); 	//Extract the command lines from the specified file.
	for(z=1; z<lengthOf(Listing);z++){		//All the command lines are sequentially executed.
						Arguments = Listing[z];
						if(Listing[z] != ""){
									runMacro(getDirectory("macros")+File.separator()+"ACT_Batch"+File.separator()+"ACT_Motor_CommandLine.txt", Arguments);
								}
						}
}
