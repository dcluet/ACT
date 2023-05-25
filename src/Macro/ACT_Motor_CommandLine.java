macro 'ACT'{

	VersionM = '1.42_2023_05_12';

	/*
	Major modifications:
		Adapt the Code for newest version of ImageJ and Fiji > 1.53
		Updates in comments.
		Comments in english.
	*/


	/*
	System monitoring:
		The monitoring of the local system will be 'finger-printed' in the
		report file allowing easier identification of version-dependent bugs.
	*/

	// Get local OS name
	OSname = getInfo('os.name');
	// Get local OS version
	OSversion = getInfo('os.version');	
	// Get local system architecture (32/64bit)
	OSarch = getInfo('os.arch');
	// Get local OS patch version
	OSpatch = getInfo('sun.os.patch.level');	
	// Get local Java version
	JavaVersion = getInfo('java.version');
	// Get local Java runtime environment version
	JavaRTVersion = getInfo('java.runtime.version');
	// Get local Java virtual machine version
	JavaVMVersion = getInfo('java.vm.version');
	// Get user name
	UserName = getInfo('user.name');
	// Get local ImageJ version
	IJV = getVersion();

	/*
	Reminder:
		For a same IJ version we have already seen some delay for new function's
		implementation (Mac vs Win vs Linux).
	*/

	// Get date and starting time of the analysis
	getDateAndTime(year, month, dayOfWeek,
				   dayOfMonth, hour, minute, second, msec);

	// Inactivate all display except main IJ window that will show progress and
	// status of the analyses
	setBatchMode(true);

	// Retrieve arguments (as a string) from the command line
	Input = getArgument();
	// Extracting all parameters
	MonInput =  split(Input, '\t');

	//Path -> Movie
	Path1 = MonInput[0];
	// Verification that the movie to analyze is present at the specified path
	F = File.exists(Path1);

	// Stop the macro if the file is not existing
	if(F!=1){
		exit('File does not exist\n' + Path1);
	}

	// Xref position zygote
	Xz = parseFloat(MonInput[1]);
	// Yref position zygote
	Yz = parseFloat(MonInput[2]);
	// Width of the zygote
	Wz = parseFloat(MonInput[3]);
	// Heigth of the zygote
	Hz = parseFloat(MonInput[4]);
	// Movie resolution
	Reso = parseFloat(MonInput[5]);
	// Real beginning of the movie
	Sstart = parseFloat(MonInput[6]);
	// Origine for segragation of the centrosome 
	Origine= parseFloat(MonInput[7]);
	// Real end of the movie
	Send = parseFloat(MonInput[8]);
	// Timegap between frames
	Temps = parseFloat(MonInput[9]);
	// X coordinate of anterior centrosome in 1st frame
	XantD = parseFloat(MonInput[10]);
	// Y coordinate of anterior centrosome in 1st frame 
	YantD = parseFloat(MonInput[11]);
	// X coordinate of anterior centrosome in last frame 
	XantF = parseFloat(MonInput[12]);
	// Y coordinate of anterior centrosome in last frame 
	YantF = parseFloat(MonInput[13]);
	// Sens of analysis for anterior centrosome
	SensAnt = parseFloat(MonInput[14]);
	// Box type for anterior centrosome  
	BoxAnt = parseFloat(MonInput[15]);
	// Diameter of the Scanning Circle (dsc) for the anterior centrosome
	DCAnt = parseFloat(MonInput[16]);
	// Height of the Boundary Box (hbb) for the anterior centrosome
	LPAnt = parseFloat(MonInput[17]);
	// X coordinate of anterior centrosome in 1st frame
	XpostD = parseFloat(MonInput[18]);
	// Y coordinate of anterior centrosome in 1st frame 
	YpostD = parseFloat(MonInput[19]);
	// X coordinate of anterior centrosome in last frame 
	XpostF = parseFloat(MonInput[20]);
	// Y coordinate of anterior centrosome in last frame 
	YpostF = parseFloat(MonInput[21]);
	// Sens of analysis for posterior centrosome
	SensPost = parseFloat(MonInput[22]);
	// Box type for anterior centrosome
	BoxPost = parseFloat(MonInput[23]);
	// Diameter of the Scanning Circle (dsc) for the posterior centrosome
	DCPost = parseFloat(MonInput[24]);
	// Height of the Boundary Box (hbb) for the posterior centrosome
	LPPost = parseFloat(MonInput[25]);
	// Extension of the movie (.tif, .mov,...)
	Extension = MonInput[26];

	// Declaration of the variables
	// Set the font for the output movie
	setFont('Arial', 14, 'bold');
	// Reference width for the Focus Square (pix)
	LargFenetreR = 120;
	// Reference Height for the Boundary Box (hbb pix)
	LargPlotProfileR = 20;
	// Reference Diameter of the Searching Circle (dsc pix)
	DiametreCercleR = 60;
	// Resolution of reference (µm/pix)
	ResRef = 0.129;
	// Adapt -to the actual resolution- of the radius of the circle calculating 
	// the pixel variance
	Radius = round(5 * ResRef / Reso);  
	// Reference width of the zygote
	LRef =782;
	// Reference height of the zygote
	HRef =518;
	// Calculating the number of frame to analyze	
	NFrames = Send - Sstart + 1;
	// Create the array that will contain the real frame number
	// (before cutting of the movie)
	FrameNumber = newArray(NFrames);
	
	// Feed the FrameNumber array
	for(nf = 0; nf< NFrames; nf++){
		FrameNumber[nf]=Sstart+nf;
	}
	
	// Create the array that will contain the time value corresponding
	// to each frame
	TimeValue = newArray(NFrames);
	// Feed the TimeValue array
	for(tv = 0; tv< NFrames; tv++){
		TimeValue[tv] = d2s((FrameNumber[tv] - Origine) * Temps, 2);
	}

	// Create the array that will contain the X positions of the 
	// anterior centrosome
	AntXValue = newArray(NFrames);
	// Create the array that will contain the Y positions of the
	// anterior centrosome 
	AntYValue = newArray(NFrames);
	// Create the array that will contain the X positions of the
	// posterior centrosome
	PostXValue = newArray(NFrames);
	// Create the array that will contain the Y positions of the
	// posterior centrosome 
	PostYValue = newArray(NFrames);
	// Create the array that will contain the distance between the
	// two centrosomes 
	DistValue = newArray(NFrames);
		
	// Open the movie
	// Depending on the extension type the Quicktime plugin or the
	// classical IJ opener are used
	if(Extension == '.mov'){
		run('Using QuickTime...', 'open=[' + Path1 + '] convert');
	}else{
		open(Path1);
	}

	// Get the title of the movie
	ImageCourante = getTitle();
		
	// Get rid of Dimensions
	getDimensions(width, height, channels, slices, frames);
	Stack.setXUnit('pixel');
	cmd = 'channels=' + channels + ' slices=' + slices;
	cmd += ' frames=' + frames;
	cmd += ' pixel_width=1 pixel_height=1 voxel_depth=1.0000000';
	run('Properties...', cmd);
		
	// Crop the Zygote
	// Draw the rectangle using the user's parameters
	makeRectangle(Xz, Yz, Wz, Hz);
	// Crop the movie
	run('Crop');
		
	// Update the values for the width and the Height of the movie	
	H = getHeight();
	W = getWidth();
		
	/*
		Calculate the ratio between the reference zygote and the actual one
		Will be used to adapt all research parameters
	*/
	ratio = H/HRef;
		
	// Cut the movie using the MontageFilm function
	MontageFilm(Sstart, Send);
		
	// Calculate the dimension of the zygote (in µm)	
	LargZyg = W * Reso;
	HautZyg = H * Reso;

	// Launch the tracking of the anterior centrosome	
	// Identify the sens for the tracking process
	if (SensAnt == 1){
		// Track in forward direction
		AntCoord = FORWARD('Anterior',
						   XantD,
						   YantD,
						   XantF,
						   YantF,
						   BoxAnt,
						   DCAnt,
						   LPAnt);
		// Retrieve the X positions
		AntXValue = split(AntCoord[0], '\t');
		// Retrieve the Y positions
		AntYValue = split(AntCoord[1], '\t');
		// Retrieve the width of the analysis window
		LargFenetreA = parseFloat(AntCoord[2]);
		// Retrieve the deviation (RMSD) between the final position found and
		// the one expected (manually entered by the user)
		RMSDAnt = Reso*parseFloat(AntCoord[3]);
	}else{
		// Track in reverse direction
		AntCoord = REVERSE('Anterior',
						   XantF,
						   YantF,
						   XantD,
						   YantD,
						   BoxAnt,
						   DCAnt,
						   LPAnt);
		// Retrieve the X positions
		AntXValue = split(AntCoord[0], '\t');
		// Retrieve the Y positions
		AntYValue = split(AntCoord[1], '\t');
		// Retrieve the width of the analysis window
		LargFenetreA = parseFloat(AntCoord[2]);
		// Retrieve the deviation (RMSD) between the final position found and
		// the one expected (manually entered by the user)
		RMSDAnt = Reso*parseFloat(AntCoord[3]);
	}
		
	// Launch the tracking of the posterior centrosome	
	if (SensPost == 1){
		// Identify of the sens for the tracking process
		// Track in forward direction
		PostCoord = FORWARD('Posterior',
							XpostD,
							YpostD,
							XpostF,
							YpostF,
							BoxPost,
							DCPost,
							LPPost);
		// Retrieve the X positions
		PostXValue = split(PostCoord[0], '\t');
		// Retrieve the Y positions
		PostYValue = split(PostCoord[1], '\t');
		// Retrieve the width of the analysis window
		LargFenetreP = parseFloat(PostCoord[2]);
		// Retrieve the deviation (RMSD) between the final position found and
		// the one expected (manually entered by the user)
		RMSDPost = Reso*parseFloat(PostCoord[3]);
	}else{
		// Track in reverse direction
		PostCoord = REVERSE('Posterior',
							XpostF,
							YpostF,
							XpostD,
							YpostD,
							BoxPost,
							DCPost,
							LPPost);
		// Retrieve the X positions
		PostXValue = split(PostCoord[0], '\t');
		// Retrieve the Y positions
		PostYValue = split(PostCoord[1], '\t');
		// Retrieve the width of the analysis window
		LargFenetreP = parseFloat(PostCoord[2]);
		// Retrieve the deviation (RMSD) between the final position found and
		// the one expected (manually entered by the user)
		RMSDPost = Reso*parseFloat(PostCoord[3]);
	}	
	
	// Calculate the deviation fror both final position compared to the expected
	// one (relative scoring function)
	RMSDTot = sqrt((pow(RMSDAnt, 2) + pow(RMSDPost, 2)) / 2);

	//	Creation of the output files
	// Preparation of the movie for output
	selectWindow(ImageCourante);
	run('RGB Color');

	/* 
		The ImageJ File.open function lets the file only acessible to ImageJ. 
		Even if print orders have been done, the file will be empty and not 
		acessible in case of crash of the code. To avoid this problem the txt
		report file is created with File.open, inprinted with an empty string
		and closed. All lines will then be added using the append function.
		In case of crash, data can be retrieved.
	*/
		
	P = Path1 + '_REPORT.txt';
	Report = File.open(P);
	print(Report, 'ACT analysis');
	File.close(Report);

	// Creation of the report txt file header	
	File.append(' ', P);
	File.append('User name: ' + UserName, P);
	File.append(' ', P);
	File.append('System: ', P);
	File.append('\tOperating system: ' + OSname, P);
	File.append('\tVersion: ' + OSversion, P);
	File.append('\tService Pack/Patch: ' + OSpatch, P);
	File.append('\tArchitecture: ' + OSarch, P);
	File.append(' ', P);
	File.append('Current Java: ', P);
	File.append('\tVersion: ' + JavaVersion, P);
	File.append('\tRunTime Environment Version: ' + JavaRTVersion, P);
	File.append('\tVM Version: ' + JavaVMVersion, P);
	File.append(' ', P);
	File.append('Current ImageJ: ', P);
	File.append('\tVersion: ' + IJV, P);
	File.append('\tACT Version: ' + VersionM, P);
	File.append(' ', P);
	File.append('ANALYSIS', P);
	File.append(' ', P);
	File.append('Analyzed movie:\t' + Path1 + '\n', P);
	File.append('Day of Analysis\t' + year + '_' + month + 1 + '_' + dayOfMonth,
				P);
	File.append(' ', P);
	File.append('Movie starting frame\t' + Sstart, P);
	File.append('Frame t=0\t' + Origine, P);
	File.append('Movie ending frame\t' + Send, P);
	File.append(' ', P);
	line = 'Crop coordinates (pix):\t' + Xz + '\t' + Yz;
	line += '\t' + (Xz + Wz - 1) + '\t' + (Yz + Hz-1);
	File.append(line, P);
	File.append(' ', P);
	File.append('Quality estimation', P);
	File.append('RMSD on references points = ' + RMSDTot + ' um', P);
	File.append('RMSD on anterior ending position = ' + RMSDAnt + ' um', P);
	File.append('RMSD on posterior ending position = ' + RMSDPost + ' um', P);
	File.append(' ', P);
	File.append('Research parameters', P);
	head = 'Engine' + '\t' + 'Box type' + '\t' + 'Direction' + '\t';
	head += 'Diameter of the Scanning Circle (dsc)' + '\t';
	head += 'Height of the Boundary Box (hbb)';
	File.append(head, P);  
	
	if(SensAnt == 1){
		DirAnt = 'FORWARD';
	}else{
		DirAnt = 'REVERSE';
	}
	
	if(SensPost == 1){
		DirPost = 'FORWARD';
	}else{
		DirPost = 'REVERSE';
	}
	
	if (BoxAnt == 1){
		BoxTAnt = 'Retrograde';
	}else{
		BoxTAnt = 'Anterograde';
	}
	
	if (BoxPost == 1){
		BoxTPost = 'Retrograde';
	}else{
		BoxTPost = 'Anterograde';
	}

	ant = 'Anterior engine\t' + BoxTAnt + '\t' + DirAnt;
	ant += '\t' + DCAnt + '\t' + LPAnt;
	File.append(ant, P); 
		
	post = 'Posterior engine\t' + BoxTPost + '\t' + DirPost;
	post += '\t' + DCPost + '\t' + LPPost;
	File.append(post, P); 
	File.append(' ', P);
	File.append(' ', P);
	File.append('START\t' + hour + ':' + minute + '\n', P);
	File.append(' ', P);
	File.append('Resolution (um/pix):\t' + Reso, P);
	File.append('Zygote Length (pix)\t' + W + '\tZygote Height (pix)\t' + H, P);
	zyg = 'Zygote Length (um)\t' + LargZyg + '\tZygote Height (um)\t' + HautZyg;
	File.append(zyg, P);
	File.append('', P);
	File.append('Frame' 
				+ '\t' + 'Time (sec)' 
				+ '\t' + 'Anterior X (pix)' 
				+ '\t' + 'Anterior Y (pix)' 
				+ '\t' +'Anterior X (um)' 
				+ '\t' + 'Anterior Y (um)' 
				+ '\t' + 'Anterior X (%)' 
				+ '\t' + 'Anterior Y(%)' 
				+ '\t' + 'Posterior X (pix)' 
				+ '\t' + 'Posterior Y (pix)' 
				+ '\t' + 'Posterior X (um)' 
				+ '\t' + 'Posterior Y (um)' 
				+ '\t' + 'Posterior X (%)' 
				+ '\t' + 'Posterior Y(%)'
				+ '\t' + 'Spindle length (pix)'
				+ '\t' + 'Spindle length (um)'
				+ '\t' + 'Spindle length (%)', P);
	
	// End of the header creation
			
	// Positioning on the movie the reference starting and ending positions
	// given by the users (cirrcles filled with yellow) 
	selectWindow(ImageCourante);
	setForegroundColor(255, 255, 0);	
	setSlice(1);		
	makeOval(XantD - 6, YantD - 6, 12, 12);
	run('Fill', 'slice');
	makeOval(XpostD - 6, YpostD - 6, 12, 12);
	run('Fill', 'slice');
	setSlice(nSlices());		
	makeOval(XantF - 6, YantF - 6, 12, 12);
	run('Fill', 'slice');
	makeOval(XpostF - 6, YpostF - 6, 12, 12);
	run('Fill', 'slice');

	// Loop creating the table of values and displaying the position of the
	// centrosomes on the output movie			
	for(i = 0; i<NFrames; i++){
		// Show the progess to the user in IJ main window
		showStatus('Creating report for frame '+ i);
		// Calculate the distance between the two centrosomes
		DistValue[i] = sqrt(pow((parseFloat(AntXValue[i]) -
								 parseFloat(PostXValue[i])), 2) +
							pow((parseFloat(AntYValue[i]) -
								 parseFloat(PostYValue[i])), 2));
			
		// Preparing the actual row		
		Texte = ''+ FrameNumber[i] 
				+ '\t' + parseFloat(TimeValue[i])
				+ '\t' + parseFloat(AntXValue[i])
				+ '\t' + parseFloat(AntYValue[i])
				+ '\t' + d2s(Reso*(parseFloat(AntXValue[i]) - (W / 2)), 2)
				+ '\t' + d2s(Reso*(parseFloat(AntYValue[i]) - (H / 2)), 2)
				+ '\t' + d2s(100*(parseFloat(AntXValue[i]) - (W / 2)) / W, 2)
				+ '\t' + d2s(100*(parseFloat(AntYValue[i]) - (H / 2)) / H, 2)
				+ '\t' + parseFloat(PostXValue[i])
				+ '\t' + parseFloat(PostYValue[i])
				+ '\t' + d2s(Reso*(parseFloat(PostXValue[i]) - (W / 2)), 2)
				+ '\t' + d2s(Reso*(parseFloat(PostYValue[i]) - (H / 2)), 2)
				+ '\t' + d2s(100*(parseFloat(PostXValue[i]) - (W / 2)) / W, 2)
				+ '\t' + d2s(100*(parseFloat(PostYValue[i]) - (H / 2)) / H, 2)
				+ '\t' + d2s(parseFloat(DistValue[i]), 2)
				+ '\t' + d2s(Reso*parseFloat(DistValue[i]), 2)
				+ '\t' + d2s(100*parseFloat(DistValue[i]) / W, 2);
		// Replace the '.' with ',' for french excel version.
		Texte = replace(Texte, '.', ',');
		
		// For later version make it conditional	
		// Update the report file						
		File.append(Texte, P);			

		// Select the correct frame of the movie
		selectWindow(ImageCourante);
		setSlice(i + 1);
			
		// Set the colorcode
		cyan = newArray(0, 50, 50);
		magenta = newArray(50, 0, 50);
			
		// Calculate the size of the boundary boxes and the scan circle
		// (in pix adapted to the actual size and resolution of the zygote)
		// hbb for anterior centrosome 		
		LargPlotProfileA = round(LargPlotProfileR * ratio) * LPAnt;
		// hbb for posterior centrosome
		LargPlotProfileP = round(LargPlotProfileR * ratio) * LPPost;
		// dsc for anterior centrosome 
		DiametreCercleA = round(DiametreCercleR * ratio) * DCAnt;
		// dsc for posterior centrosome
		DiametreCercleP = round(DiametreCercleR * ratio) * DCPost;
			
		// Display the boundary box and scanning circle on the frame using the
		// function DRAWBOX
		if ((i>1) && (i<nSlices() - 1)){
			if(SensAnt == 1){
				prog = -1;
			}else{
				prog = 1;
			}
			
			DRAWBOX(BoxAnt,
					LargPlotProfileA + DiametreCercleA + 2 * Radius + 2,
					LargPlotProfileA,
					parseFloat(AntXValue[i + prog]),
					parseFloat(AntYValue[i + prog]),
					cyan);
			
			if(SensPost == 1){
				prog = -1;
			}else{
				prog = 1;
			}
			
			DRAWBOX(BoxPost,
					LargPlotProfileP + DiametreCercleP + 2 * Radius + 2,
					LargPlotProfileP,
					parseFloat(PostXValue[i + prog]),
					parseFloat(PostYValue[i + prog]),
					magenta);
		}
			
		/* 
			Display the centrosomes on the frame
			The color is dark before the origine frame and light after
		*/

		setLineWidth(2);
		if(parseFloat(TimeValue[i]) > 0){
			setColor(0, 125, 125);	
		} else{
			setColor(0, 255, 255);	
		}
		
		drawOval(parseFloat(AntXValue[i]) - 2,
				 parseFloat(AntYValue[i]) - 2,
				 5,
				 5);
		
		if(parseFloat(TimeValue[i]) > 0){
			setColor(125, 0,125);	
		} else{
			setColor(255,0,255);	
		}

		drawOval(parseFloat(PostXValue[i]) - 2,
				 parseFloat(PostYValue[i]) - 2,
				 5,
				 5);
		
		setColor(0, 175, 175);
		drawOval(parseFloat(AntXValue[i])-(ratio * DCAnt * DiametreCercleR)/2,
				 parseFloat(AntYValue[i])-(ratio * DCAnt * DiametreCercleR)/2,
				 (ratio * DCAnt * DiametreCercleR),
				 (ratio * DCAnt * DiametreCercleR));
		
		setColor(175,0,175);
		drawOval(parseFloat(PostXValue[i])-(ratio * DCPost * DiametreCercleR)/2,
				 parseFloat(PostYValue[i])-(ratio * DCPost * DiametreCercleR)/2,
				 (ratio*DCPost*DiametreCercleR),
				 (ratio*DCPost*DiametreCercleR));

		if (i>=1){
			// Print the 'Anterior', 'Posterior', and position labels		
			setFont('Arial', 14, 'bold');
			setForegroundColor(0, 50, 50);
			drawString('Anterior',
					   parseFloat(AntXValue[i-1]) - LargFenetreA / 2,
					   parseFloat(AntYValue[i-1]) - LargFenetreA / 2 - 20);
			setForegroundColor(50, 0, 50);
			drawString('Posterior',
					   parseFloat(PostXValue[i-1]) - LargFenetreP / 2,
					   parseFloat(PostYValue[i-1]) - LargFenetreP / 2 - 20);
			setForegroundColor(50, 50, 50);
			setFont('Arial', 10);
			drawString('('+ d2s(Reso * (parseFloat(AntXValue[i]) - W / 2), 2) + ';'
					   + d2s(Reso * (parseFloat(AntYValue[i]) - H / 2), 2) + ')',
					   parseFloat(AntXValue[i - 1]) - LargFenetreA / 2,
					   parseFloat(AntYValue[i - 1]) - LargFenetreA / 2 - 4);
			drawString('('+ d2s(Reso * (parseFloat(PostXValue[i]) - W / 2), 2) + ';'
					   + d2s(Reso * (parseFloat(PostYValue[i]) - H / 2), 2) + ')',
					   parseFloat(PostXValue[i - 1]) - LargFenetreP / 2,
					   parseFloat(PostYValue[i - 1]) - LargFenetreP / 2 - 4);
				
			// Draw the focus squares				
			setLineWidth(1);
			setColor(0, 100, 100);
			drawRect(parseFloat(AntXValue[i - 1]) - LargFenetreA / 2,
					 parseFloat(AntYValue[i - 1]) - LargFenetreA / 2,
					 LargFenetreA,
					 LargFenetreA);
			
			setColor(100, 0, 100);
			drawRect(parseFloat(PostXValue[i - 1]) - LargFenetreP / 2,
					 parseFloat(PostYValue[i - 1]) - LargFenetreP / 2,
					 LargFenetreP,
					 LargFenetreP);
		}
			
		// Display the spindle (white line before origine frame and black after)
		// and the distance in µm between the two centrosomes
		if(TimeValue[i] > 0){
			setColor(0, 0, 0);	
		} else{
			setColor(250, 250, 250);	
		}

		setLineWidth(3);
		drawLine(parseFloat(AntXValue[i]),
				 parseFloat(AntYValue[i]),
				 parseFloat(PostXValue[i]),
				 parseFloat(PostYValue[i]));
		Aff = 'Distance ' + d2s(Reso * parseFloat(DistValue[i]), 2) + 'um';
		setForegroundColor(0, 0, 0);
		setFont('Arial', 12, 'bold');
		AffL = getStringWidth(Aff);
			
		Yaff = parseFloat(AntYValue[i]) + LargFenetreA / 2 + 20;
					
		drawString(Aff,
				   parseFloat(AntXValue[i]) + (parseFloat(PostXValue[i]) - 
				   parseFloat(AntXValue[i])) / 2 - AffL / 2,
				   Yaff);
			
		//Display the time value corresponding to the current frame
		setForegroundColor(0, 0, 0);
		setFont('Arial', 10);
		drawString(d2s(parseFloat(TimeValue[i]), 2) + ' sec', 4, 14);				
	}

	// End of the Loop creating the table of values and displaying the position
	// of the centrosomes on the output movie

	// Get the date and time corresponding to the end of the process
	getDateAndTime(year, month, dayOfWeek,
				   dayOfMonth, hour, minute, second, msec);
	
	// Update the report file. Last entry!
	File.append('\nEnd\t' +hour+':'+minute, P);
		
	// Duplication of the median frame and copy it in first position with
	// analysis parameters
	
	// Identification of the frame number
	Sref = round(nSlices()/2);
	// Select the movie
	selectWindow(ImageCourante);
	// select first frame
	setSlice(1);
	// Select the content of the frame
	makeRectangle(0, 0, getWidth(), getHeight());
	// Copy the content
	run('Copy');
	setSlice(1);
	// Duplicate after frame 1
	run('Add Slice');
	run('Paste');
	// Select the median frame
	setSlice(Sref);
	// Select the content of the frame
	makeRectangle(0, 0, getWidth(), getHeight());
	// Copy the content
	run('Copy');
	setSlice(1);
	// Duplicate after frame 1
	run('Add Slice');
	run('Paste');
	setSlice(1);
	// Remove the extra frame1
	run('Delete Slice');
	setSlice(1);
	// Set the font to display informations
	setFont('Arial', 14, 'bold');
	// Create title indicating which frame is presented
	Titre1 = 'POSITION OF THE CENTROSOMES AT ' + TimeValue[Sref - 2] + 'sec';
	// Calculate the width of the title (pix)
	L = getStringWidth(Titre1);
	// Calculate the position to center the title
	x = ((getWidth() - L) / 2) -1; 
		
	// Prepare a contrasted rectangle to print the title
	// Set the foreground color
	setForegroundColor(200, 200, 200);
	// Position the rectangle
	makeRectangle(x - 2, getHeight() - 20 - 14 - 2, L + 4, 14 + 2);
	// Fill it in light grey
	run('Fill', 'slice');
	// Set the border color
	setForegroundColor(0, 0, 0);
	// draw the black color
	run('Draw', 'slice');
	// Print the Title
	drawString(Titre1, x, getHeight() - 20);
	
	// Reference height for the rectangle containing the parameters
	HauteurRef = 50;
	// Set the font for the display of the parameters
	setFont('Arial', 14, 'bold');	

	// Print the parameters for the tracking of the anterior centrosome
	TAntFS = 'Parameters';
	TAntBB = 'hbb= ' + LPAnt;
	TAntSC = 'dsc= ' + DCAnt;
	LT1 = getStringWidth(TAntFS);
	LT2 = getStringWidth(TAntBB);
	LT3 = getStringWidth(TAntSC); 
	Dim1 = newArray(LT1, LT2, LT3);
	Array.getStatistics(Dim1, min, max, mean, stdDev);
	makeRectangle(10, HauteurRef - 18, max + 8, 58);
	setForegroundColor(200, 200, 200);
	run('Fill', 'slice');
	setForegroundColor(0, 75, 75);
	run('Draw', 'slice');
	drawString(TAntFS, 14, HauteurRef);	
	drawString(TAntBB, 14, HauteurRef + 20);	
	drawString(TAntSC, 14, HauteurRef + 40);	
		
	// Print the parameters for the tracking of the posterior centrosome
	TPostFS = 'Parameters';
	TPostBB = 'hbb= ' + LPPost;
	TPostSC = 'dsc= ' + DCPost;
	LT1 = getStringWidth(TPostFS);
	LT2 = getStringWidth(TPostBB);
	LT3 = getStringWidth(TPostSC); 
	Dim1 = newArray(LT1, LT2, LT3);
	Array.getStatistics(Dim1, min, max, mean, stdDev);
	makeRectangle(getWidth() - max - 18, HauteurRef - 18, max + 8, 58);
	setForegroundColor(200, 200, 200);
	run('Fill', 'slice');
	setForegroundColor(75, 0, 75);
	run('Draw', 'slice');
	drawString(TPostFS, getWidth() - max - 14, HauteurRef);	
	drawString(TPostBB, getWidth() - max - 14, HauteurRef + 20);	
	drawString(TPostSC, getWidth() - max - 14, HauteurRef + 40);
		
	// Creation of the graphs
	// Get the number TimeL of time value of the tracking
	TimeL = lengthOf(TimeValue);
	// Create a new array with TimeL values to receive the Time values
	TimeValueN = newArray(TimeL);
	// Create a new array with TimeL values to receive the X position
	// of the anterior centrosome
	AntXValueN = newArray(TimeL);
	// Create a new array with TimeL values to receive the Y position
	// of the anterior centrosome
	AntYValueN = newArray(TimeL);
	// Create a new array with TimeL values to receive the X position
	// of the posterior centrosome
	PostXValueN = newArray(TimeL);
	// Create a new array with TimeL values to receive the Y position 
	// of the posterior centrosome
	PostYValueN = newArray(TimeL);
	// Create a new array with TimeL values to receive the distace values
	// between the centrosomes
	DistValueN = newArray(TimeL);
		
	/*
		This step is necessary to convert all the values presented as string
		into numbers that ImageJ can handle for the creation of the graphs
	*/ 

	// Conversion of all string into numbers in the new arrays
	for(k=0; k<TimeL; k++){
		TimeValueN[k]= parseFloat(TimeValue[k]);
		AntXValueN[k] = parseFloat(AntXValue[k]);
		AntYValueN[k] = parseFloat(AntYValue[k]);
		PostXValueN[k] = parseFloat(PostXValue[k]);
		PostYValueN[k] = parseFloat(PostYValue[k]);
		DistValueN[k] = parseFloat(DistValue[k]);
	}
		
	// Get movie dimension to generate graphs with the correct size	
	selectWindow(ImageCourante);
	H = getHeight();
	W = getWidth();
		
	// Creation of the graph 'X positions'	
	Plot.create('X positions', 'Time (sec)', 'X position (pix)');
	Plot.setFrameSize(W * 0.8, H * 0.8);
	Plot.setLimits(TimeValueN[0], TimeValueN[TimeL-1], 0, W);
	Plot.setColor('cyan');
	Plot.addText('Anterior', 0.02, 0.1);
	Plot.add('dots', TimeValueN,  AntXValueN);
	Plot.setColor('magenta');
	Plot.addText('Posterior', 0.02, 0.2);
	Plot.add('dots', TimeValueN,  PostXValueN);
	Plot.show();
		
	// Copy the graph and duplicate it 10 times in the output movie
	// (user will have time to see it)
	makeRectangle(0,0, W, H);
	run('Copy');
	selectWindow(ImageCourante);
		
	for(S=1; S<=10; S++){
		setSlice(1);
		run('Add Slice');
		setForegroundColor(255, 255, 255);
		makeRectangle(0, 0, getWidth(), getHeight());
		run('Fill', 'slice');
		run('Paste');
	}
	
	// Close the graph window
	selectWindow('X positions');
	close();


	// Creation of the graph 'Y positions'	
	Plot.create('Y positions', 'Time (sec)', 'Y position (pix)');
	Plot.setFrameSize(W * 0.8, H * 0.8);
	Plot.setLimits(TimeValueN[0], TimeValueN[TimeL - 1], 0, H);
	Plot.setColor('cyan');
	Plot.addText('Anterior', 0.02, 0.1);
	Plot.add('dots', TimeValueN,  AntYValueN);
	Plot.setColor('magenta');
	Plot.addText('Posterior', 0.02, 0.2);
	Plot.add('dots', TimeValueN,  PostYValueN);
	Plot.show();
		
	// Copy the graph and duplicate it 10 times in the output movie
	// (user will have time to see it)
	makeRectangle(0, 0, W, H);
	run('Copy');
	selectWindow(ImageCourante);
			
	for(S=1; S<=10; S++){
		setSlice(1);
		run('Add Slice');
		setForegroundColor(255, 255, 255);
		makeRectangle(0, 0, getWidth(), getHeight());
		run('Fill', 'slice');
		run('Paste');
	}
	
	// Close the graph window
	selectWindow('Y positions');
	close();

	// Creation of the graph 'Distance between centrosomes'	
	Plot.create('Distance between asters (pix)',
				'Time (sec)',
				'Distance between asters (pix)');
	Plot.setFrameSize(W * 0.8, H * 0.8);
	Plot.setLimits(TimeValueN[0], TimeValueN[TimeL-1], 0, H);
	Plot.setColor('black');
	Plot.add('dots', TimeValueN,  DistValueN);
	Plot.show();
		
	// Copy the graph and duplicate it 10 times in the output movie
	// (user will have time to see it)
	makeRectangle(0, 0, W, H);
	run('Copy');
	selectWindow(ImageCourante);
	
	for(S=1; S<=10; S++){
		setSlice(1);
		run('Add Slice');
		setForegroundColor(255, 255, 255);
		makeRectangle(0, 0, getWidth(), getHeight());
		run('Fill', 'slice');
		run('Paste');
	}
	
	// Close the graph window
	selectWindow('Distance between asters (pix)');
	close();

	selectWindow(ImageCourante);
	scale = 200/getHeight();
	WR = round(scale*getWidth());
	// run('Scale...', 'x=- y=- z=1.0 width='+WR+' height='+200
	// +' depth='+nSlices()+' interpolation=Bilinear average process 
	// create title=Reduced');
	saveAs('Tiff', Path1 + '_ASTERS.tif');	//Save the output movie
	close();
	//selectWindow(ImageCourante);
	//close();
			
function FORWARD(Type, Xdebut, Ydebut, Xfin, Yfin, Box, DC, LP){	
	// Get the number of frame in the movie
	n = nSlices();
	// Create the array that will contain all X positions
	Xtable = newArray(n);
	// Create the array that will contain all Y positions		
	Ytable = newArray(n);
	// Calculate the ratio between the current movie and the reference one
	ratio = H/HRef;
	
	// Adapt the height of the boundary box (hbb) to the current movie
	LargPlotProfile = round(LargPlotProfileR * ratio) * LP;
	// Adapt the diameter of the scanning circle (dsc)
	DiametreCercle = round(DiametreCercleR * ratio) * DC;
	// Calculating the width of the focus square using the size of the boundary
	// box and of the scanning circle
	LargFenetre = LargPlotProfile+DiametreCercle + 2 * Radius + 2;
	
	// Create the Temp image that will receive the subsection of the frame
	// to be analyzed
	newImage('Temp', '8-bit White', LargFenetre, LargFenetre, 1);

	// The first value of the array containing the x values is fed with manual
	// position of the centrosome on starting frame
	Xtable[0] = Xdebut;
	// The first value of the array containing the y values is fed with manual
	// position of the centrosome on starting frame 
	Ytable[0] = Ydebut; 
	
	/*
		Warning the first index in the array is 0 when the first frame of a 
		movie is 1... thus all calculation of index are done with -1
	*/
	
	// Scan the frames in forward direction
	for(i=2;i<=n;i++){
		// Use the previous x position as reference point
		X = Xtable[i - 2];
		// Use the previous y position as reference point
		Y = Ytable[i - 2];
		// Select the movie
		selectWindow(ImageCourante);
		// Select the current frame
		setSlice(i);

		/*
			launch the tracking of the centrosome using the function
			RechercheAster with position and size of the research square,
			the width of the boundary box, the diameter of the scanning circle,  
			the current frame number, the total number of frame and the Box size	
		*/
		Coord= RechercheAster(X-LargFenetre/2,
							  Y-LargFenetre/2,
							  LargFenetre,
							  LargPlotProfile,
							  DiametreCercle,
							  i,
							  n,
							  Box);
		
		// Retrieve the X position from the tracking function
		Xtable[i - 1] = Coord[0];
		// Retrieve the Y position from the tracking function
		Ytable[i - 1] = Coord[1];
	}
	
	// Create a string variable that will contain and x positions for transfer
	// to the main program
	ReportX = '';
	// Create a string variable that will contain and y positions for transfer
	// to the main program 
	ReportY = '';
	for(i=0; i<n; i++){
		// feed the x positions string
		ReportX = ReportX + '\t' + Xtable[i];
		// feed the y poistions string
		ReportY = ReportY + '\t' + Ytable[i];
	}

	// close the Temp image
	selectWindow('Temp');
	close();
	
	// Calculating the deviation between the final position (tracked) and the
	// expected one (human) of the centrosome
	RMSD = sqrt((pow(Xtable[n - 1] - Xfin, 2) + 
				 pow(Ytable[n - 1] - Yfin, 2)) / 1);
	
	// Prepare a string array to transfert all results to main program
	result = newArray(ReportX, ReportY, '' + LargFenetre, '' + RMSD);
	return result;
}	
		
function REVERSE(Type,
				 Xdebut,
				 Ydebut,
				 Xfin,
				 Yfin,
				 Box,
				 DC,
				 LP){	
	/*
		ANALYZE THE MOVIE IN REVERSE MODE
	*/

	// Get the number of frame in the movie
	n = nSlices();
	// Create the array that will contain all X positions	
	Xtable = newArray(n);
	// Create the array that will contain all Y positions
	Ytable = newArray(n);
	// Calculate the ratio between the current movie and the reference one
	ratio = H / HRef;

	// Adapt the height of the boundary box (hbb) to the current movie
	LargPlotProfile = round(LargPlotProfileR * ratio) * LP;
	// Adapt the diameter of the scanning circle (dsc)
	DiametreCercle = round(DiametreCercleR * ratio) * DC;
	// Calculating the width of the focus square using the size of the boundary
	// box and of the scanning circle
	LargFenetre = LargPlotProfile + DiametreCercle + 2 * Radius + 2;

	// Create the Temp image that will receive the subsection of the frame
	// to be analyzed
	newImage('Temp', '8-bit White', LargFenetre, LargFenetre, 1);

	// The first value of the array containing the x values is fed with manual
	// position of the centrosome on ending frame
	Xtable[n - 1] = Xdebut;
	// The first value of the array containing the y values is fed with manual
	// position of the centrosome on ending frame 
	Ytable[n - 1] = Ydebut; 
			
	/*
		Warning the first index in the array is 0 when the first frame of a
		movie is 1... thus all calculation of index are done with -1
	*/

	// Scan the frames in reverse direction
	for(i=n - 1; i>0; i--){
		// Use the next x position as reference point
		X = Xtable[i];
		// Use the next y position as reference point
		Y = Ytable[i];

		// Select the movie
		selectWindow(ImageCourante);
		// Select the current frame
		setSlice(i);

		/*
			launch the tracking of the centrosome using the function
			RechercheAster with position and size of the research square,
			the width of the boundary box, the diameter of the scanning circle,  
			the current frame number, the total number of frame and the Box size	
		*/

		Coord = RechercheAster(X - LargFenetre / 2,
							   Y - LargFenetre / 2,
							   LargFenetre,
							   LargPlotProfile,
							   DiametreCercle,
							   i,
							   n,
							   Box);
		// Retrieve the X position from the tracking function
		Xtable[i - 1] = Coord[0];
		// Retrieve the Y position from the tracking function
		Ytable[i - 1] = Coord[1];	
	}
	
	// Create a string variable that will contain and x positions for
	// transfer to the main program
	ReportX = '';
	// Create a string variable that will contain and y positions for
	// transfer to the main program
	ReportY = '';

	for(i=0; i<n; i++){
		// Feed the x positions string
		ReportX = ReportX + '\t' + Xtable[i];
		// Feed the y poistions string
		ReportY = ReportY + '\t' + Ytable[i];
	}

	// Close the Temp image
	selectWindow('Temp');
	close();

	// Calculate the deviation between the final position (tracked) and the
	// expected one (human) of the centrosome
	RMSD = sqrt((pow(Xtable[0] - Xfin, 2) + pow(Ytable[0] - Yfin, 2)) / 1);
	// Prepare a string array to transfert all results to main program	
	result = newArray(ReportX, ReportY, ''+LargFenetre, ''+RMSD);
	return result;
}


function RechercheAster(x,
						y,
						LargFenetre,
						LargPlotProfile,
						DiametreCercle,
						i,
						n,
						AorP){
	/*
		SEARCH THE ASTER ON THE IMAGE
	*/

	// Initialize the minimal value for variance in 8bit
	MinValue = 255;
	// Create an array taht will contain the coordinates of the
	// new position of the centrosome
	Coord = newArray(2);
	
	// Position the focus square
	makeRectangle(x, y, LargFenetre, LargFenetre);
	// Copy the content
	run('Copy');

	// Select the Temp window
	selectWindow('Temp');
	// Paste the content of the research square
	run('Paste');

	// Calculate the pixel variance within the window with the adapted Radius
	run('Variance...', 'radius=' + Radius);
	// Display the ID of the centrosome/frame currently analyzed
	// in IJ main window		
	showStatus('Analyzing image ' + i + ' of ' + n);
	// Show the progress of the tracking in IJ main window
	showProgress(i/n);
	
	//Position the boundary box depending on the box type
	if (AorP ==1){
		debut = (LargFenetre - LargPlotProfile) / 2;
		fin = LargFenetre / 2 + 1;
	}else{
		debut = LargFenetre / 2 - 1;
		fin = (LargFenetre + LargPlotProfile) / 2;
	}
	
	// Loop for the identification of the minal variance value
	for (X = debut ; X < fin; X+=1){
		for(Y = (LargFenetre - LargPlotProfile) / 2;
			Y < (LargFenetre+LargPlotProfile) / 2;
			Y+=1){
				// Select the temporary image
				selectWindow('Temp');
				// Position the scanning circle
				makeOval(X - DiametreCercle / 2,
						 Y - DiametreCercle / 2,
						 DiametreCercle,
						 DiametreCercle);
				List.setMeasurements
				// Measure the mean value of the variance within the 
				// scanning circle
				M = List.getValue('Mean');								
				
				/*
					If the mean value is the new minimal value the cordinates 
					are attributed to the new position of the centrosome
				*/

				if (M<MinValue){								
					MinValue = M;
					Coord[0] = X+x;
					Coord[1] = Y+y;		
				}
		}
	}
	return Coord;
}	
		

		
function MontageFilm(Sstart, Send){
	/*
		CREATE A NEW MOVIE BY REMOVING THE EXTRA SLICES
	*/
		N = nSlices+1;
		// Delete all frames present after the ending frame
		for (i = Send + 1; i < N; i++){
			setSlice(Send + 1);
			run('Delete Slice');
		}

		// Delete all frames present before the starting one
		for (i = 1; i < Sstart; i++){
			setSlice(1);
			run('Delete Slice');
		}
	}

function DRAWBOX(Box,
				 LF,
				 LP,
				 X,
				 Y,
				 color){	
	/*
		DRAW THE BOUNDARY BOX
	*/

	// Determination of the type of the box and its position in the
	// reasearch square
	if(Box==1){
		debutX = (LF - LP) / 2;
		finX = LF / 2 + 1;
	}else{
		debutX = LF / 2 - 1;
		finX = (LF + LP) / 2;
	}

	debutY = (LF - LP) / 2;
	finY = (LF + LP) / 2;
	x = (X - LF / 2) + debutX;	
	y =	(Y - LF / 2) + debutY;
	w = finX - debutX + 1;	
	h = finY - debutY + 1;
	CoordCrop = newArray(x, y, w, h);
	selectWindow(ImageCourante);
	makeRectangle(CoordCrop[0],CoordCrop[1], CoordCrop[2],CoordCrop[3]);
	setForegroundColor(color[0], color[1], color[2]);
	run('Fill', 'slice');
}

}	
