				/* flyAtlas2.js 27.01.2025 */
				
// Writes start of (hidden) form setting values of options
function startForm()
{	
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");
	
	// show SDs if relevant
	if (document.getElementById('errors_0') 
			&& document.getElementById('errors_0').checked)
	{
		var hiddenErrorsField = document.createElement("input");
		hiddenErrorsField.setAttribute("type", "hidden");
		hiddenErrorsField.setAttribute("name", "errors");
		hiddenErrorsField.setAttribute("value", "errors");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenErrorsField);
	}
	
	// show Whole Fly data, if relevant
	if (document.getElementById('whole_0') 
			&& document.getElementById('whole_0').checked)
	{
		var hiddenWholeField = document.createElement("input");
		hiddenWholeField.setAttribute("type", "hidden");
		hiddenWholeField.setAttribute("name", "whole");
		hiddenWholeField.setAttribute("value", "whole");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenWholeField);
	}
	
	// show M v. F data, if relevant
	if (document.getElementById('mf_0') 
			&& document.getElementById('mf_0').checked)
	{
		var hiddenMFField = document.createElement("input");
		hiddenMFField.setAttribute("type", "hidden");
		hiddenMFField.setAttribute("name", "mf");
		hiddenMFField.setAttribute("value", "mf");				// Because we have to send a String to set a boolean
		form.appendChild(hiddenMFField);
	}
		
	return form;
}

		// Create hidden submission forms for different queries and appropriate pages //
		
function sendSearchGeneForm() 
{
	var gene = document.getElementById('inputField').value;
	// idtype handled separately because of smart check of gene value

	if(gene=="")
	{
		alert("Please enter a gene identifier");
	}
	else
	{ 
		var form = startForm();	
		
			// identifier field for gene search form (will be null at start)
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "gene");		
		form.appendChild(hiddenSearchField);		
			// gene id etc (from and for) gene text field
		var hiddenGeneField = document.createElement("input");	
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);		
		form.appendChild(hiddenGeneField);		
			// idtype from (and for) radio button choice
		var idtype = getIDType();		
		var hiddenIDField = document.createElement("input");	
		hiddenIDField.setAttribute("type", "hidden");
		hiddenIDField.setAttribute("name", "idtype");
		hiddenIDField.setAttribute("value", idtype);	
		form.appendChild(hiddenIDField);
		
		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchBulkForm()
{
	var genes = document.getElementById('idList').value;
	
	if(genes == "")
	{
		alert("Please enter a list of gene identifiers");
	}
	else
	{ 
		var form = startForm();
		// identifier field for bulk search form (will be null at start)
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "bulk");		
		form.appendChild(hiddenSearchField);
		
		var hiddenGeneField = document.createElement("input");	
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "geneList");
		hiddenGeneField.setAttribute("value", genes);		
		form.appendChild(hiddenGeneField);
		
		document.body.appendChild(form);
		form.submit();	
	}
}

function sendSearchMidgutForm() 
{
	var gene = document.getElementById('inputField').value;
	// idtype handled separately because of smart check of gene value

	if(gene=="")
	{
		alert("Please enter a gene identifier");
	}
	else
	{ 
		var form = startForm();	
		
			// identifier field for gene search form (will be null at start)
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "midgut");		
		form.appendChild(hiddenSearchField);		
			// gene id etc (from and for) gene text field
		var hiddenGeneField = document.createElement("input");	
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);		
		form.appendChild(hiddenGeneField);		
			// idtype from (and for) radio button choice
		var idtype = getIDType();
		var hiddenIDField = document.createElement("input");	
		hiddenIDField.setAttribute("type", "hidden");
		hiddenIDField.setAttribute("name", "idtype");
		hiddenIDField.setAttribute("value", idtype);	
		form.appendChild(hiddenIDField);
		
		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchProfileForm() 
{
	var gene = document.getElementById('inputField').value;
	if(gene=="")
	{
		alert("Please enter a gene identifier.");
	}
	else
	{
		var form = startForm();
			// identifier field for profile search form
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "profile");
		form.appendChild(hiddenSearchField);
			// name of gene entered in text field
		var hiddenGeneField = document.createElement("input");
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);
		form.appendChild(hiddenGeneField);
			// idtype from (and for) radio button choice
		var idtype = getIDType();
		var hiddenIDField = document.createElement("input");	
		hiddenIDField.setAttribute("type", "hidden");
		hiddenIDField.setAttribute("name", "idtype");
		hiddenIDField.setAttribute("value", idtype);	
		form.appendChild(hiddenIDField);	
			// tissues for profile
		var tissues = document.getElementById('tissues').value;
		var tissHidden = document.createElement("input");
		tissHidden.setAttribute("type", "hidden");
		tissHidden.setAttribute("name", "tissues");
		tissHidden.setAttribute("value", tissues);
		form.appendChild(tissHidden);		
			// Pearson or Spearman
		var hiddenPearsonField = document.createElement("input");
		hiddenPearsonField.setAttribute("type", "hidden");
		hiddenPearsonField.setAttribute("name", "correlation");
		if(document.getElementById('pearson').checked)
		{
			hiddenPearsonField.setAttribute("value", "pearson");
		}
		else
		{
			hiddenPearsonField.setAttribute("value", "spearman");
		}
		form.appendChild(hiddenPearsonField);
			// r statistic cutoff
		var rcut = document.getElementById('rcut').value;
		var rCutHidden = document.createElement("input");
		rCutHidden.setAttribute("type", "hidden");
		rCutHidden.setAttribute("name", "rcut");
		rCutHidden.setAttribute("value", rcut);
		form.appendChild(rCutHidden);
		// Max No. of results to display (maxdisplayed)
		var hiddenMaxField = document.createElement("input");
		var maxdisplayed = document.getElementById('maxdisplayed').value;
		hiddenMaxField.setAttribute("type", "hidden");
		hiddenMaxField.setAttribute("name", "maxdisplayed");
		hiddenMaxField.setAttribute("value", maxdisplayed);
		form.appendChild(hiddenMaxField);

		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchGoForm() 
{	
	var go = document.getElementById('inputField').value;

	if(go=="")
	{
		alert("Please enter a search term.");
	}
	else
	{
		var form = startForm();
			// identifier field for go (gene ontology = category) search form		
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "go");
		form.appendChild(hiddenSearchField);
			// gene ontology term for text field
		var hiddenGoField = document.createElement("input");
		hiddenGoField.setAttribute("type", "hidden");
		hiddenGoField.setAttribute("name", "go");
		hiddenGoField.setAttribute("value", go);
		form.appendChild(hiddenGoField);
			// radio button choice for GO
		var radioGo = getRadioGo();
		var hiddenRadioField = document.createElement("input");
		hiddenRadioField.setAttribute("type", "hidden");
		hiddenRadioField.setAttribute("name", "radioGo");
		hiddenRadioField.setAttribute("value", radioGo);		
		form.appendChild(hiddenRadioField);
			// Max No. of results to display (maxdisplayed)
		var hiddenMaxField = document.createElement("input");
		var maxdisplayed = document.getElementById('maxdisplayed').value;
		hiddenMaxField.setAttribute("type", "hidden");
		hiddenMaxField.setAttribute("name", "maxdisplayed");
		hiddenMaxField.setAttribute("value", maxdisplayed);
		form.appendChild(hiddenMaxField);
		
		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchGroupForm()
{
	var form = startForm();
	// identifier field for group (subset of category) search form		
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "group");
	form.appendChild(hiddenSearchField);
		// groupID
	var hiddenGroupField = document.createElement("input");
	var groupID = document.getElementById('groupID').value;	
	hiddenGroupField.setAttribute("type", "hidden");
	hiddenGroupField.setAttribute("name", "groupID");
	hiddenGroupField.setAttribute("value", groupID);
	form.appendChild(hiddenGroupField);
	
	document.body.appendChild(form);
	form.submit();	
}


function sendSearchTopForm()	// no text capture here
{
	var form = startForm();
		// identifier field for top search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "top");
	form.appendChild(hiddenSearchField);
		// sex 
	var hiddenTissueField = document.createElement("input");
	var sex = document.getElementById('stage').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "sex");
	hiddenTissueField.setAttribute("value", sex);
	form.appendChild(hiddenTissueField);
		// tissue ID 
	var hiddenTissueField = document.createElement("input");
	var tissue = document.getElementById('tissue').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "tissue");
	hiddenTissueField.setAttribute("value", tissue);
	form.appendChild(hiddenTissueField);
		// order term (enrich/abund)
	var hiddenOrderField = document.createElement("input");
	var order = document.getElementById('order').value;
	hiddenOrderField.setAttribute("type", "hidden");
	hiddenOrderField.setAttribute("name", "order");
	hiddenOrderField.setAttribute("value", order);
	form.appendChild(hiddenOrderField);
		// Max No. of results to display (maxdisplayed)
	var hiddenMaxField = document.createElement("input");
	var maxdisplayed = document.getElementById('maxdisplayed').value;
	hiddenMaxField.setAttribute("type", "hidden");
	hiddenMaxField.setAttribute("name", "maxdisplayed");
	hiddenMaxField.setAttribute("value", maxdisplayed);
	form.appendChild(hiddenMaxField);
		// Whether search for microRNAs rather than protein genes (microgene)
	if (document.getElementById('microgene') && document.getElementById('microgene').checked)
	{
		var hiddenMicroField = document.createElement("input");
		hiddenMicroField.setAttribute("type", "hidden");
		hiddenMicroField.setAttribute("name", "microgene");
		hiddenMicroField.setAttribute("value", "microgene");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenMicroField);
	}

	document.body.appendChild(form);
	form.submit();
}

		// Create hidden submission forms for links to different sections //
// repeated stuff — only parameters are SDs, Whole Body, M v. F for initial pages
function startToForm()
{
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");			
	
	// show SDs
	if (document.getElementById('errors_0') 
			&& document.getElementById('errors_0').checked)
	{
		var hiddenErrorsField = document.createElement("input");
		hiddenErrorsField.setAttribute("type", "hidden");
		hiddenErrorsField.setAttribute("name", "errors");
		hiddenErrorsField.setAttribute("value", "errors");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenErrorsField);
	}	
	// show Whole Fly data
	if (document.getElementById('whole_0') 
			&& document.getElementById('whole_0').checked)
	{
		var hiddenWholeField = document.createElement("input");
		hiddenWholeField.setAttribute("type", "hidden");
		hiddenWholeField.setAttribute("name", "whole");
		hiddenWholeField.setAttribute("value", "whole");		// Because we have to send a String to set a boolean
		form.appendChild(hiddenWholeField);
	}
	// show M v. F data
	if (document.getElementById('mf_0') 
			&& document.getElementById('mf_0').checked)
	{
		var hiddenMFField = document.createElement("input");
		hiddenMFField.setAttribute("type", "hidden");
		hiddenMFField.setAttribute("name", "mf");
		hiddenMFField.setAttribute("value", "mf");				// Because we have to send a String to set a boolean
		form.appendChild(hiddenMFField);
	}
	
	return form;
}

function toGeneForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "gene");	// gene page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toGOForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");	
	hiddenField.setAttribute("value", "go");		// GO page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toGroupForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");	
	hiddenField.setAttribute("value", "group");		// group in GO page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toTopForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "top");		// top page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toProfileForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "profile");		// Profile
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toMidgutForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "midgut");	// gene page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toHomeForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "home");		// home page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toFeedbackForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "contact");	// feedback page named as contact
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toHelpForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "help");		// documentation page named as help
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}


// Smart check for correct idtype choice
// determines name of gene and auto-assigns FBgn, FBtr or CG choices to fbgn, fbtr or cgnum, resp.
function getIDType()
{
	var idtype;		// idtype choice
	var rawInput = document.getElementById('inputField').value;
	var input = rawInput.trim();

	if(input.substring(0,4) == "FBgn")
	{
		idtype = "fbgn";
	}
	else if(input.substring(0,4) == "FBtr")
	{
		idtype = "fbtr";
	}
	else if(input.substring(0,2) == "CG")
	{
		idtype = "cgnum";
	}
	else if(document.getElementById('symbol').checked)
	{
		idtype = "symbol";
	}
	else if(document.getElementById('name').checked)
	{
		idtype = "name";
	}
	else if(document.getElementById('cgnum').checked)
	{
		idtype = "cgnum";
	}
	else if(document.getElementById('fbtr').checked)
	{
		idtype = "fbtr";
	}
	else
	{
		idtype = "fbgn";
	}	
	return idtype;
}

//determines checked radio button for use in hidden form fields in Category page
function getRadioGo()
{
	var radioGo;		// radiobutton choice
	if(document.getElementById('goTerm').checked)
	{
		radioGo = "goTerm";
	}
	else if(document.getElementById('goID').checked)
	{
		radioGo = "goID";
	}
	else
	{
		radioGo = "goFree";
	}	
	return radioGo;
}

// Submit forms by hitting 'enter' key (code 13)
function geneKey(e)
{
	if (e.keyCode == 13) 
	{
		var theStyle = document.getElementById("controlsA").style;
		if(theStyle.display == "block")
		{
			sendSearchGeneForm();
		}
	}
}
function goKey(e)
{	
	if (e.keyCode == 13) 
	{
		sendSearchGoForm();
	}
}
function groupKey(e)
{	
	if (e.keyCode == 13) 
	{
		sendSearchGroupForm();
	}
}
function topKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchTopForm();
	}
}
function profileKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchProfileForm();
	}
}
function midgutKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchMidgutForm();
	}
}

	// toggle all function (smart)
	function toggleAll(linkAllID, numResults, defText, altText)
	{
		// toggle self
		var linkEle = document.getElementById(linkAllID);	// this is the icon itself
		var oldText = linkEle.textContent;
		var newText;
		if(oldText === "▽")
		{
			newText = "▷";
		}
		else
		{
			newText = "▽";
		}
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
		
		// toggle slaves
		for (var i=0; i < numResults; i++) 
		{	
			toggleSlaves(linkAllID, ("bt_"+i), ("hs_"+i), defText, altText);
		}
	}
	
	// sets all according to the master button, irrespective of current state (unlike toggleConcealed)
	function toggleSlaves(linkAllID, linkID, targetID, defText, altText)
	{
		var masterText = document.getElementById(linkAllID).textContent;
		
		if(document.getElementById(targetID) !=null)
		{
			var theStyle = document.getElementById(targetID).style;
			if (masterText === "▷")
			{
				theStyle.display = "none";
				newText = defText;
			}
			else
			{
				theStyle.display = "block";
				newText = altText;
			}
			var linkEle = document.getElementById(linkID);
			linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
		}
	}
	
	//  takes ID of link element, hide/show target div, and default and alternative text to do hide/show and text change
	function toggleConcealed(linkID, targetID, defText, altText)
	{
		var theStyle = document.getElementById(targetID).style;
		if (theStyle.display == "block")
		{
			theStyle.display = "none";
			newText = defText;
		}
		else
		{
			theStyle.display = "block";
			newText = altText;
		}
		var linkEle = document.getElementById(linkID);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}	

	// takes class of target <tr> s for hide/show
	function toggleRow(rowClass)
	{
		var myClasses = document.getElementsByClassName(rowClass);
		
		for (var i=0; i < myClasses.length; i++) 
		{
			var theStyle = myClasses[i].style;
			if(theStyle.display == 'none')
			{
				theStyle.display = '';
			}
			else
			{
				theStyle.display = 'none';			
			}
		}	
	}
	
	// Specific class for hide/showing Male/Female cf columns in table
	function toggleColumns(tableClass)
	{
		var tbs = document.getElementsByClassName(tableClass);
		for (var t=0; t < tbs.length; t++) 
		{
			var rows = tbs[t].getElementsByTagName('tr');

			// first row is header with 5 cells — hide/show third cell
			var first = rows[0].getElementsByTagName('th');		
			var style3 = first[3].style;			
			if(style3.display == 'none')
			{
				style3.display = '';
			}
			else
			{
				style3.display = 'none';			
			}
		
			// Now rest of rows except hidden pair at end — hide/show 5th and 6th cell
			for (var i=1; i<rows.length-2; i++)
			{
				var cells;
				if(i==1)
				{
					cells = rows[i].getElementsByTagName('th');
				}
				else
				{
					cells = rows[i].getElementsByTagName('td');
				}		
				var style5 = cells[5].style;
				var style6 = cells[6].style;			
				if(style5.display == 'none')
				{
					style5.display = '';
					style6.display = '';
				}
				else
				{
					style5.display = 'none';
					style6.display = 'none';			
				}
			}
		
			// penultimate row — toggle colspan between 7 and 9
			var spacer = rows[rows.length-2].getElementsByTagName('td');   	
			// last row — hide/show 5th and 6th cell
			var last = rows[rows.length-1].getElementsByTagName('td');		
			var style5 = last[5].style;
			var style6 = last[6].style;			
			if(style5.display == 'none')
			{
				spacer[0].colSpan = 9;
				style5.display = '';
				style6.display = '';
			}
			else
			{
				spacer[0].colSpan = 7;	
				style5.display = 'none';
				style6.display = 'none';		
			} 	
		}
	}
	
	// allows hide/show of target columns for M v. F to be set directly on creation of page
	function setColumns(tableClass, on)
	{
		var tbs = document.getElementsByClassName(tableClass);
		for (var i=0; i < tbs.length; i++) 
		{
			var rows = tbs[i].getElementsByTagName('tr');
			var first = rows[0].getElementsByTagName('th');			
			if(on == true)
			{
				first[3].style.display = '';
			}
			else
			{
				first[3].style.display = 'none';			
			}	
			for (var j=1; j<rows.length-2; j++)
			{	
				var cells;
				if(j==1)
				{
					cells = rows[j].getElementsByTagName('th');
				}
				else
				{
					cells = rows[j].getElementsByTagName('td');
				}
				var style5 = cells[5].style;
				var style6 = cells[6].style;
				if(on == true)
				{
					style5.display = '';
					style6.display = '';					
				}
				else
				{
					style5.display = 'none';
					style6.display = 'none';								
				}
			}
			// penultimate row — toggle colspan between 7 and 9
			var spacer = rows[rows.length-2].getElementsByTagName('td');   	
			// last row — hide/show 5th and 6th cell
			var last = rows[rows.length-1].getElementsByTagName('td');		
			var style5 = last[5].style;
			var style6 = last[6].style;			
			if(on)
			{
				spacer[0].colSpan = 9;
				style5.display = '';
				style6.display = '';
			}
			else
			{
				spacer[0].colSpan = 7;	
				style5.display = 'none';
				style6.display = 'none';		
			} 
		}		
	}
	
	// allows hide/show of target row to be set directly on creation of page
	function setRow(rowClass, on)
	{
		var myClasses = document.getElementsByClassName(rowClass);
		for (var i=0; i < myClasses.length; i++) 
		{
			var theStyle = myClasses[i].style;
			if(on == true)
			{
				theStyle.display = '';
			}
			else
			{
				theStyle.display = 'none';			
			}
		}		
	}
	
	// takes id of target <div> to hide show contents (e.g. for icon w. no change of text)
	function toggleDiv(target)
	{
		var targetStyle = document.getElementById(target).style;

		if (targetStyle.display == "block")
		{
			targetStyle.display = "none";
		}
		else
		{
			targetStyle.display = "block";		
		}	
	}
	
	// allows closing a hide/show div with a x box
	function closeDiv(target)
	{
		document.getElementById(target).style.display = "none";
	}
	
	// takes class of target <span> to hide show contents (e.g. for icon w. no change of text)
	function toggleSpan(spanClass) 
	{
		var myClasses = document.querySelectorAll(spanClass);

		for (var i=0; i < myClasses.length; i++) 
		{
			var theStyle = myClasses[i].style;
			if(theStyle.display == 'inline')
			{
				theStyle.display = 'none';
			}
			else
			{
				theStyle.display = 'inline';			
			}
		}
	}
	
	// allows hide/show of target span to be set directly on creation of page
	function setSpan(spanClass, on)
	{
		var myClasses = document.querySelectorAll(spanClass);
		for (var i=0; i < myClasses.length; i++) 
		{
			var myClasses = document.querySelectorAll(spanClass);	// ???
			var theStyle = myClasses[i].style;
			if(on == true)
			{
				theStyle.display = 'inline';
			}
			else
			{
				theStyle.display = 'none';			
			}
		}		
	}
	
	// Functions for reciprocal hide/show of two controls div (hard-coded) — called in 'onload'

	// creates link on line with vis text if there is div with hidden text
	function createLink(page)
	{
		var hidDivIDA = "controlsA";			// id of initially visible controls div
		var hidDivIDB = "controlsB";			// id of initially hidden controls div
		var linkID = "expand";					// id for link - generated by js
		var visDivID = "switchControls";		// id of div with vis text to which link ele is added
		var linkText;
		
		var visDiv = document.getElementById(visDivID);		// div to add link ele to
		var hidDivB = document.getElementById(hidDivIDB);	// div to hide/show			
		var hidDivA = document.getElementById(hidDivIDA);	// div to hide/show
		
		if(page == "gene")
		{
			linkText = "◀ Single Entry / Batch Entry switch ▶";
		}
		else if (page == "category")
		{
			linkText = "◀ Text Entry / Group List switch ▶";		
			// Need to correct switch links if page is returned with group results
			if(document.getElementById("groupRes"))	// Page with group search results
			{
				var hidDivB = document.getElementById(hidDivIDA);	// div to hide/show			
				var hidDivA = document.getElementById(hidDivIDB);	// div to hide/show
			}
		}

		// create 'a' element with js link to hideShow function and append to visible div
		var hsLink = document.createElement("a");
		hsLink.id = linkID;	// provide link with id to ref for text change 
		// construct the ahref as the js hideShow()
		hsLink.href = "javascript:hideShow('" + hsLink.id + "','" + hidDivB.id + "','" + hidDivA.id + "');" ;
		// add linked text to element and add element to div
		hsLink.appendChild(document.createTextNode(linkText));
		visDiv.appendChild(hsLink);
	}		
	
	// takes ids of link element and hide/show target divs to do hide/shows
	function hideShow(link, targetB, targetA)
	{
		// does the hide/show stuff on the targets
		BStyle = document.getElementById(targetB).style;
		AStyle = document.getElementById(targetA).style;
		
		if (BStyle.display == "block")
		{
			BStyle.display = "none";
			AStyle.display = "block";
		}
		else
		{
			BStyle.display = "block";
			AStyle.display = "none"
		}	
	}
	
	// synchronizes checked state checkboxes: used where checkbox makes global change (e.g. for errors)
	function synchBoxes(ckbox, theClass)
	{
		var boxes = document.getElementsByTagName("input");

		for(var i=0; i<boxes.length; i++)
		{
			if(ckbox.checked && boxes[i].classList == theClass)
			{
				boxes[i].checked = true;
			}
			else if (!ckbox.checked && boxes[i].classList == theClass)
			{
				boxes[i].checked = false;
			}
		}
	}
	
	// open Links page in popup (modified to add version No for appropriate css file
	function loadLinks(fbgn, cg, npid)
	{
		var versionNo = 2;	// hard-coded for FlyAtlas2
		var url = "/FlyLinks/index.html?fbgn=" + fbgn + "&cg=" + cg + "&dinerID=" + npid + "&versionNo=" + versionNo;
		var w = 720;
		var h = 560;
		openHelp(url, "External Fly Links", w, h);
	}
	
	// opens UCSC Brower link/info page in popup
	function linkToUCSC(id, locus, mir)
	{
		var url = "/FlyBrowse/index.html?id=" + id + "&locus=" + locus + "&mir=" + mir;
		var w = 720;
		var h = 520;
		openHelp(url, "Link to UCSC browser", w, h);		
	}
	
	// open FlyBase report for transcript in separate window
	function linkToFBtr(fbtr)	
	{
		var url = "https://flybase.org/reports/" + fbtr + ".html";
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "FlyBase Transcript Report for " + fbtr;
		window.open(url, name, args);
	}
	
	// open FlyBase report for gene in separate window
	function linkToFBgn(fbgn)	
	{
		var url = "https://flybase.org/reports/" + fbgn + ".html";
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "FlyBase Gene Report for " + fbgn;
		window.open(url, name, args);
	}
	
	// Send batch query using retrieved paralogues
/*	function linkToFlyAtlas(para)
	{
		para = para.replace(/,/g, '%0D%0A');				// replace comma by EOL for FA2 batch entry
		var url = "https://motif.mvls.gla.ac.uk/FlyAtlas2/?search=bulk&geneList=" + para;	// flyatlas2.org doesn't work.
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "Drosophila homologue(s)";
		window.open(url, name, args);
	}*/
	// Send to FlyPara to generate List/Button window	
	function listParalogues(id) 
	{
		var url = "/FlyPara/index.html?id=" + id;
		var w = 460;
		var h = 600;
		var name = "Drosophila Paralogue(s)";
		openHelp(url, name, w, h);
	}
	
	function linkToPaper(url, name)	
	{
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		window.open(url, name, args);
	}
	
	function midgutToGene(gene, idtype)		// changed url to mvls
	{
		var url = "https://motif.mvls.gla.ac.uk/FlyAtlas2/index.html?search=gene&gene=" + gene + "&idtype=" + idtype + "#mobileTargetG";
		var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
		var name = "FlyAtlas 2";
		window.open(url, name, args);
	}
	
	// Sends SVG to Servlet SVGreflector for returning as down-loadable file
	function sendSVG(resultNum) 
	{
		var svgText = document.getElementById("svg_" + resultNum).innerHTML;			// id of div holding SVG
		var geneID = document.getElementById("graphID_" + resultNum).textContent;		// id of span holding gene name
		
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", "/SVGreflector/image.svg");
		form.setAttribute("accept-charset", "UTF-8");	

		var hiddenSVGField = document.createElement("input");	
		hiddenSVGField.setAttribute("type", "hidden");
		hiddenSVGField.setAttribute("name", "svgText");
		hiddenSVGField.setAttribute("value", svgText);		
		form.appendChild(hiddenSVGField);
		
		var hiddenNameField = document.createElement("input");	
		hiddenNameField.setAttribute("type", "hidden");
		hiddenNameField.setAttribute("name", "graphName");
		hiddenNameField.setAttribute("value", geneID);		
		form.appendChild(hiddenNameField);
		
		document.body.appendChild(form);
		form.submit();
	}
	
	function openHelp(url, name, w, h) 
	{ 
	   var args = 'width=' + w + ','
	   + 'height=' + h + ','
	   + 'toolbar=0,'
	   + 'location=0,'
	   + 'directories=0,'
	   + 'status=yes,'
	   + 'menubar=0,'
	   + 'scrollbars=1,'
	   + 'resizable=yes';	 
	   if (parseInt(navigator.appVersion) >= 4)
	   {
		   xposition = (screen.width - w)/2;
		   yposition = (screen.height - h)/2;  
		   args += ','
			   + 'screenx=' + xposition + ',' //NN
			   +  'screeny=' + yposition + ',' //NN
			   +  'left=' + xposition + ',' //IE
			   +  'top=' + yposition; //IE
	    }
	   window.open(url, name, args);
	}

	// sets focus to input field, if present
	function setFocus() 
	{
		if(document.getElementById("inputField"))
		{
			var input = document.getElementById("inputField");
			input.focus();
			return;
		}
		else
		{
			return;		
		}
	}
	
	// function to try to append a location hash to a url dynamically generated by submit button
	function setHash(locHash)
	{
		if(document.getElementById(locHash))
		{
			window.location.hash = locHash;
		}
		else
		{
			window.location.hash = "";	
		}
	}
	
	// When the user scrolls down 20px from the top of the document, show the button
	function scrollFunction() 
	{
		let mybutton = document.getElementById("upButton");
		if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) 
		{
			mybutton.style.display = "block";
		} 
		else 
		{
			mybutton.style.display = "none";
		}
	}

	// When the user clicks on the button, scroll to the top of the document
	function topFunction() 
	{
		document.body.scrollTop = 0;
		document.documentElement.scrollTop = 0;
	}
	