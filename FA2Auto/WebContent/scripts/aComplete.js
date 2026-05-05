// aComplete.js for FA2Auto 10.05.2016
// last update 24.08.2022

var xmlHttp;
var completeDiv;
var inputField;
var menuTable;
var menuTableBody;
var searchType;	// type of search: symbol, name, CGnum, FBtr or FBgn

function createXMLHttpRequest() 
{
	if (window.ActiveXObject) 
	{
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	else if (window.XMLHttpRequest) 
	{
		xmlHttp = new XMLHttpRequest();                
	}
}

function initVars() 
{
	inputField = document.getElementById("gene");            
	menuTable = document.getElementById("menuTable");
	completeDiv = document.getElementById("popup");
	menuTableBody = document.getElementById("menuTableBody");
	
	if(document.getElementById("symbol").checked == true)
	{
		searchType = "symbol";
	}
	else if(document.getElementById("name").checked == true)
	{
		searchType = "name";
	}
	else if(document.getElementById("cgnum").checked == true)
	{
		searchType = "cgnum";
	}
	else if(document.getElementById("fbgn").checked == true)
	{
		searchType = "fbgn";
	}
	else if(document.getElementById("fbtr").checked == true)
	{
		searchType = "fbtr";
	}
	else
	{
		searchType = "none";	
	}
}

function findNames() 
{
	initVars();
	if (inputField.value.length > 0) 
	{
		createXMLHttpRequest();
		var encFieldValue = encodeURIComponent(inputField.value);			
		var url = "/FA2Auto/index.html?gene=" + encFieldValue
				+ "&searchType=" + searchType;

		xmlHttp.open("GET", url, true);
		xmlHttp.onreadystatechange = callback;
		xmlHttp.send(null);
	} 
	else 
	{
		clearNames();
	}
}

function callback() 
{
	if (xmlHttp.readyState == 4) 
	{
		if (xmlHttp.status == 200) 
		{
			setNames(xmlHttp.responseXML.getElementsByTagName("name"));
		} 
		else if 
		(xmlHttp.status == 204)
		{
			clearNames();
		}
	}
}

function setNames(the_names) 
{            
	clearNames();
	var size = the_names.length;
	setOffsets();

	var row, cell, txtNode;
	for (var i = 0; i < size; i++) 
	{
		var nextNode = the_names[i].firstChild.data;
		row = document.createElement("tr");
		cell = document.createElement("td");
		
		cell.onmouseout = function() {this.className='mouseOver';};
		cell.onmouseover = function() {this.className='mouseOut';};
		cell.onclick = function() { populateName(this); } ;                             

		txtNode = document.createTextNode(nextNode);
		cell.appendChild(txtNode);
		row.appendChild(cell);
		menuTableBody.appendChild(row);
	}
}

function setOffsets() 
{
	var end = inputField.offsetWidth;
	var left = calculateOffsetLeft(inputField);
	var top = calculateOffsetTop(inputField) + inputField.offsetHeight;

	completeDiv.style.border = "black 1px solid";
	completeDiv.style.left = left + "px";
	completeDiv.style.top = top + "px";
	menuTable.style.width = end + "px";
}

function calculateOffsetLeft(field) 
{
  return calculateOffset(field, "offsetLeft");
}

function calculateOffsetTop(field) 
{
  return calculateOffset(field, "offsetTop");
}

function calculateOffset(field, attr) 
{
  var offset = 0;
  while(field) 
  {
	offset += field[attr]; 
	field = field.offsetParent;
  }
  return offset;
}

function populateName(cell) 
{
	inputField.value = cell.firstChild.nodeValue;
	clearNames();
}

function clearNames() 
{
	var ind = menuTableBody.childNodes.length;
	for (var i = ind - 1; i >= 0 ; i--) 
	{
		 menuTableBody.removeChild(menuTableBody.childNodes[i]);
	}
	completeDiv.style.border = "none";
}
