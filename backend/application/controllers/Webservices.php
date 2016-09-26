<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Webservices extends CI_Controller {	
	function __construct()
	 {
	   parent::__construct();
	   $this->load->model('Webservice','',TRUE);
	   $this->load->library(array('notification'));
	   
	 }
	function index()   
	{
		$this->load->helper(array('form'));
		if($_POST)
		{
			/////////////////////  facebook login /////////////////////////////////////////////
			if(isset($_POST["Fb_login"]))
		    {
			 $this->load->library('form_validation');
			 $this->form_validation->set_rules('Fname', 'First Name', 'trim|required');
			 $this->form_validation->set_rules('Lname', 'Last Name', 'trim|required');
			 $this->form_validation->set_rules('Image', 'Image', 'trim|required');
			 $this->form_validation->set_rules('access_token', 'access token', 'trim|required');
			 $this->form_validation->set_rules('DEVICE_ID', 'DEVICE ID', 'trim|required');
			 $this->form_validation->set_rules('DEVICE_TYPE', 'DEVICE TYPE', 'trim|required');
				if($this->form_validation->run() == FALSE)
				{
				   $datas["status"] = "Fail";
				   $datas["message"] = "All Fields Are Required";
				   echo json_encode($datas);
				}
			   else
			   {   
				
					$data["fname"] = $_POST["Fname"];
					$data["lname"] = $_POST["Lname"];
					$data["userpic"] = $_POST["Image"];
					$data["loginid"] = $_POST["access_token"];
					$data["deviceid"] = $_POST["DEVICE_ID"];
					$data["devicetype"] = $_POST["DEVICE_TYPE"];
					$check = $this->Webservice->already_register_social($_POST["access_token"]);
					if($check)
					{
                            $updatedata["deviceid"] = $_POST["DEVICE_ID"];
					        $updatedata["devicetype"] = $_POST["DEVICE_TYPE"];
								 //$updatedata["count"] = 1;
							$update = $this->Webservice->updatelogincount($_POST["access_token"] , $updatedata);						
					   foreach($check as $row)
					   {  
						$datas["status"] = "success";
						$datas["msg"] = $row;
						echo json_encode($datas);      
					   }
				   }
				   else
				   {
						  if($add = $this->Webservice->User_register($data))
						  {
							 $check1 = $this->Webservice->already_register_social($_POST["access_token"]);
							 foreach($check1 as $row)
							 {
								 $updatedata["deviceid"] = $_POST["DEVICE_ID"];
					             $updatedata["devicetype"] = $_POST["DEVICE_TYPE"];
								 $updatedata["count"] = 1;
								 $update = $this->Webservice->updatelogincount($_POST["access_token"] , $updatedata);

										$datas["status"] = "success";
										$datas["msg"] = $row;
										echo json_encode($datas); 
							  }				 
							  
						  }
					   
				   }
			   }
		}
		
		/////////////////////  facebook login end    /////////////////////////////////////////////
		
		
		/////////////////////  Login Via Uni        /////////////////////////////////////////////
		
		if(isset($_POST["unilogin"]))
		{
			 $this->load->library('form_validation');
			 $this->form_validation->set_rules('userid', 'userid', 'trim|required');
			 $this->form_validation->set_rules('name', 'name', 'trim|required');
			 $this->form_validation->set_rules('pname', 'pname', 'trim|required');
				if($this->form_validation->run() == FALSE)
				{
				   $datas["status"] = "Fail";
				   $datas["message"] = "All Fields Are Required";
				   echo json_encode($datas);
				}
			   else
			   {   
		               $classname = explode(',', $_POST["name"]);
					   $proffessorname = explode(',', $_POST["pname"]);
		           
				       foreach($classname as $key => $value)
					   {
						   //print_r($classname[$key]); die;
		$check =$this->Webservice->checkclassname($_POST["userid"],str_replace(" ","",$classname[$key]));
						 if($check)
						 {
						  $add = ""; 
						 }
						 else
						 {
						  $data["user_id"] = $_POST["userid"];
						  $data["name"] = str_replace(" ","",$classname[$key]);
						  $data["proffessor"] = $proffessorname[$key]; 
                          $add = $this->Webservice->addclass($data); 
							
						 }
						  						  
					   }
				   
				       
						if($add || $add == "")
						{
							$datas["status"] = "success";
							$datas["message"] = "Class has been added successfully";
							echo json_encode($datas);
						}
						else
						{
							$datas["status"] = "Fail";
							$datas["message"] = "Something wrong in insertion . Please try again";
							echo json_encode($datas);
						}
				   
			   }
		}
		
		/////////////////////  Login Via Uni  end /////////////////////////////////////////////
		
		
		
		/////////////////////  add class   /////////////////////////////////////////////
		if(isset($_POST["Addclass"]))
		{
			 $this->load->library('form_validation');
			 $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
			 $this->form_validation->set_rules('name', 'Class Name', 'trim|required');
			 $this->form_validation->set_rules('pname', 'Proffessor Name', 'trim|required');
			 //$this->form_validation->set_rules('rollno', 'Roll Number', 'trim|required');
				if($this->form_validation->run() == FALSE)
				{
				   $datas["status"] = "Fail";
				   $datas["message"] = "All Fields Are Required";
				   echo json_encode($datas);
				}
			   else
			   {
				    $check = $this->Webservice->checkclassname($_POST["userid"] , $_POST["name"]);
					if($check)
					{
						    $datas["status"] = "Fail";
							$datas["message"] = "Please try different class name . already exist .";
							echo json_encode($datas);
					}
					else
					{
						$data["user_id"] = $_POST["userid"];
						$data["name"] = $_POST["name"];
						$data["proffessor"] = $_POST["pname"];
						//$data["rollno"] = $_POST["rollno"];
						if($this->Webservice->addclass($data))
						{
							$datas["status"] = "success";
							$datas["message"] = "Class has been added successfully";
							echo json_encode($datas);
						}
						else
						{
							$datas["status"] = "Fail";
							$datas["message"] = "Something wrong in insertion . Please try again";
							echo json_encode($datas);
						}
					}
				   
				    
			   }				   
		}
		/////////////////////  add class  end /////////////////////////////////////////////
		
		
		/////////////////////  Edit class     /////////////////////////////////////////////
		
		if(isset($_POST["Editclass"]))
		{
			 $this->load->library('form_validation');
			 $this->form_validation->set_rules('classid', 'Class Id', 'trim|required');
			 $this->form_validation->set_rules('name', 'Class Name', 'trim|required');
				if($this->form_validation->run() == FALSE)
				{
				   $datas["status"] = "Fail";
				   $datas["message"] = "All Fields Are Required";
				   echo json_encode($datas);
				}
			   else
			   {
				   $select = $this->Webservice->selectclass($_POST["classid"] , $_POST["name"]);
				   if($select)
				   {
					   foreach($select as $list)
					   {
						   $updatedata["name"] = $_POST["name"];
						   $edit = $this->Webservice->editclass($_POST["classid"] , $updatedata);
						   if($edit)
						   {
							   $eventupdatedata["name"] = $_POST["name"];
							   $updateevent = $this->Webservice->eventupdate($list->user_id , $list->name,$eventupdatedata);
								$datas["status"] = "success";
								$datas["message"] = "Class has been updated successfully . ";
								echo json_encode($datas);
						   }
						   else
						   {
								$datas["status"] = "Fail";
								$datas["message"] = "Something wrong in Edit . Please try again";
								echo json_encode($datas);
						   } 
					   }
					    
				   }
				   else
				   {
					        $datas["status"] = "Fail";
							$datas["message"] = "Sorry class not found .";
							echo json_encode($datas);
				   }
				   
				   
			   }
			
		}
		/////////////////////  Edit class end /////////////////////////////////////////////
		
		
		
		
		
		
		/////////////////////  add event   /////////////////////////////////////////////
		if(isset($_POST["Addevent"]))
		{
			 $this->load->library('form_validation');
			 $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
			 $this->form_validation->set_rules('name', 'Class Name', 'trim|required');
			 $this->form_validation->set_rules('tname', 'Topic Name', 'trim|required');
			 $this->form_validation->set_rules('location', 'Location', 'trim|required');
			 $this->form_validation->set_rules('time', 'Start Time', 'trim|required');
			 //$this->form_validation->set_rules('participants', 'Max Number Of Participants', 'trim|required');
				if($this->form_validation->run() == FALSE)
				{
				   $datas["status"] = "Fail";
				   $datas["message"] = "All Fields Are Required";
				   echo json_encode($datas);
				}
			   else
			   {
				    date_default_timezone_set($_POST["time_zone"]);
				    $data["user_id"] = $_POST["userid"];
					$data["name"] = $_POST["name"];
					$data["topicname"] = $_POST["tname"];
					$data["location"] = $_POST["location"];
					$data["starttime"] = $_POST["time"];
					//$data["participants"] = $_POST["participants"];
					$data["created"] = date('y/m/d h:i:s a');
					$data["updatedtime"] = date('y/m/d h:i:s a');
					$data["time_zone"] = $_POST["time_zone"];;
					
					$checkcourseandtopicname = $this->Webservice->checkcourseandtopicname($_POST["userid"] , $_POST["tname"]);
					if($checkcourseandtopicname)
					{
						    $datas["status"] = "Fail";
							$datas["message"] = "Please try different and Topic name . already exist .";
							echo json_encode($datas);
					}
					else
					{
						if($this->Webservice->addevent($data))
						{
							$datas["status"] = "success";
							$datas["message"] = "Event has been added successfully";
							echo json_encode($datas);
						}
						else
						{
							$datas["status"] = "Fail";
							$datas["message"] = "Something wrong in insertion . Please try again";
							echo json_encode($datas);
						}
					}
					
			   }				   
		}
		/////////////////////  add event  end /////////////////////////////////////////////
		
		/////////////////////  removeclass /////////////////////////////////////////////
		
			if(isset($_POST["removeclass"]))
			{
				 $this->load->library('form_validation');
				 $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
				 $this->form_validation->set_rules('classid', 'Class Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $removeclass = $this->Webservice->removeclass($_POST["classid"] , $_POST["userid"]);
					   if($removeclass)
					   {
						   $datas["status"] = "success";
					       $datas["message"] = "Class has been removed successfully";
					       echo json_encode($datas);
					   }
					   else
					   {
						  $datas["status"] = "Fail";
					      $datas["message"] = "Something wrong . Please try again .";
					      echo json_encode($datas); 
					   }
					   
				   }
			}
		/////////////////////  removeclass end /////////////////////////////////////////////
		
		
		/////////////////////  classlist /////////////////////////////////////////////
		
		if(isset($_POST["classlist"]))
		{
			     $this->load->library('form_validation');
				 $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $select = $this->Webservice->userclasslist($_POST["userid"]);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							  $datas["message"][] = $list; 
						   }
						   
						   echo json_encode($datas);  
					   }
					   else
					   {
						 $datas["status"] = "Fail";
					     $datas["message"] = "Sorry ! . Class list not found .";
					     echo json_encode($datas);  
					   }
				   }
		}
		
		/////////////////////  classlist end /////////////////////////////////////////////
 
        /////////////////////  my event list   /////////////////////////////////////////////
		if(isset($_POST["myeventlist"]))
		{
			     $this->load->library('form_validation');
				 $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $this->Webservice->deleteafter3hours($_POST["time_zone"]);
					   $select = $this->Webservice->usereventlist($_POST["userid"]);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							  // print_r($list->starttime); die();
							   $date = new DateTime($list->starttime);
                               $newdate = $date->format('m/d/Y H:i A');
							   
							  $datas["message"][] = array
							  (
							     "eventid" => $list->eventid,
								 "user_id" => $list->user_id,
								 "name" => $list->name,
								 "topicname" => $list->topicname,
								 "location" => $list->location,
								 "starttime" => $newdate,
								 "participants" => $this->Webservice->joincount($list->eventid),
								 "created" => $list->created
								 
							  );

                             							  
						   }
						   
						   echo json_encode($datas);  
					   }
					   else
					   {
						 $datas["status"] = "Fail";
					     $datas["message"] = "Sorry ! . Event list not found .";
					     echo json_encode($datas);  
					   }
				   }
		}
		/////////////////////  my event list end /////////////////////////////////////////////
		
		/////////////////////  searchevent /////////////////////////////////////////////
		
		if(isset($_POST["searchevent"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('name', 'Course Number', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $this->Webservice->deleteafter3hours($_POST["time_zone"]);
					  $select = $this->Webservice->searchevent($_POST["name"]); 
					  if($select)
					  {
						  $datas["status"] = "success";
						  foreach($select as $list)
						  {
							  $checkeventjoin = $this->Webservice->checkeventjoin($_POST["userid"] , $list->event_id);
							  
							  if($checkeventjoin)
							  {
								 $join = "Yes"; 
							  }
							  else
							  {
								 $join = "No";  
							  }
							  
							   $date = new DateTime($list->starttime);
                               $newdate = $date->format('m/d/Y H:i A');
							  
							$datas["message"][] = array(
							                  "eventid" => $list->event_id,
							                  "userid" => $list->userid,
											  "Course_Name" => $list->Course_Name,
											  "topic" => $list->topic,
											  "starttime" => $newdate,
											  "location" => $list->location,
											  "participants" => $this->Webservice->joincount($list->event_id),
											  "fname" => $list->fname,
											  "lname" => $list->lname,
											  "userpic" => $list->userpic,
											  "join" => $join,
											  "eventdate" => $list->eventdate
											  
							  ); 
                            							
						  }
						   echo json_encode($datas);
					  }
					  else
					  {
						 $datas["status"] = "Fail";
					     $datas["message"] = "List not found . Please try again";
					     echo json_encode($datas); 
					  }
					   
				   }
		}
		
		/////////////////////  searchevent end /////////////////////////////////////////////
		
		/////////////////////  grouplist       /////////////////////////////////////////////
		
		if(isset($_POST["grouplist"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   { 
			   
			          $allid = array();

					  $checkid = $this->Webservice->selectidfrominvite($_POST["userid"]);
					  if($checkid)
					  {
						  foreach($checkid as $listdata)
						  {
							 // print_r($listdata);
							 array_push($allid , $listdata->eventid); 
						  }
					  }
					  else
					  {
						  array_push($allid , 0);
					  }
					   //print_r($allid); die();
			          $select = $this->Webservice->grouplist($allid , $_POST["userid"]);
					  //print_r($select); die();
					  if($select)
					  {
						  
						   $datas["status"] = "success";
						   foreach($select as $list)
						    {
							  $datas["message"][] = array
							  (
							      "fname" => $list->fname,
								  "lname" => $list->lname,
								  "userpic" => $list->userpic,
								  "name" => $list->name,
								  "eventid" => $list->eventid,
								  "topicname" => $list->topicname,
								  "lastmsgtime" => $this->Webservice->lastmsgtime($list->eventid),
								  "lastmsg" => $this->Webservice->lastmsg($list->eventid),
								  "display" => $this->Webservice->groupcheck($list->eventid , $_POST["userid"]),
								  "read" => $this->Webservice->readornot($this->Webservice->lastmsgid($list->eventid),$_POST["userid"],$list->eventid)
							  ); 
						    }
						   
						   echo json_encode($datas);   
					  }
					  else
					  {
						 $datas["status"] = "Fail";
					     $datas["message"] = "Sorry ! . Group list not found .";
					     echo json_encode($datas); 
					  }
				   }
		}
		
		/////////////////////  grouplist end /////////////////////////////////////////////
		
		/////////////////////  contactlist   /////////////////////////////////////////////
		
		if(isset($_POST["contactlist"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('name', 'name', 'trim|required');
					$this->form_validation->set_rules('eventid', 'event id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   { 
			         $select = $this->Webservice->contactlist($_POST["userid"] , $_POST["name"]);
					 if($select)
					 {
						 $datas["status"] = "success";
						 foreach($select as $list)
						 {
							$datas["message"][] = array
							 (
							       //"eventid" => $list->eventid,
								   "userid"  => $list->userid,
								   "fname" => $list->fname,
								   "lname"  => $list->lname,
								   "userpic" => $list->userpic,
								   "invite"  => $this->Webservice->inviteornot($_POST["userid"],$list->userid,$_POST["eventid"])
								 
							 ); 
						 }
						 echo json_encode($datas);
					 }
					 else
					 {
						$datas["status"] = "Fail";
					    $datas["message"] = "Sorry ! . Contact list not found .";
					    echo json_encode($datas); 
					 }
				   }
		}
		
		/////////////////////  contactlist end /////////////////////////////////////////////
		
		/////////////////////  invite     /////////////////////////////////////////////
		if(isset($_POST["invite"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					$this->form_validation->set_rules('friendsid', 'Friends Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   { 
			                $data["userid"] = $_POST["userid"];
							$data["eventid"] = $_POST["eventid"];
							$data["inviteid"] = $_POST["friendsid"];
							$data["status"] = 0;
							
			               $invite = $this->Webservice->invite($data);
						   if($invite)
						   {
							 $datas["status"] = "success";
					         $datas["message"] = "Invitation has been send successfully .";
					         echo json_encode($datas);  
						   }
							else
							{
							 $datas["status"] = "Fail";
					         $datas["message"] = "Sorry ! . Something wrong Please try again .";
					         echo json_encode($datas);	
							}	
					
				   }
			
		}
		
		/////////////////////  invite end /////////////////////////////////////////////
		
		/////////////////////  accept /////////////////////////////////////////////
		
		if(isset($_POST["accept"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('inviteid', 'Invite Id', 'trim|required');
					$this->form_validation->set_rules('friendsid', 'Friends Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $data["status"] = 1;
					   $update = $this->Webservice->updateinvite($_POST["inviteid"],$_POST["friendsid"],$data);
					   if($update)
					   {
						   
						    $insertdata["eventid"] = $_POST["inviteid"];
					        $insertdata["userid"] = $_POST["friendsid"];
					        $insertdata["message"] = "";
					        $insertdata["type"] = "join";
					        $insertdata["created"] = date(" m/d/Y h:s A");
					       // $data["created"] = $_POST["date"];

					        $this->Webservice->insetchat($insertdata);
						   
						   
						   
						   $updatedata["updatedtime"] = date('y/m/d h:i:s a');
						   $this->Webservice->updateevent($_POST["inviteid"] , $updatedata);
						   
						   $datas["status"] = "success";
					       $datas["message"] = "Your invitation has been accepted successfully";
					       echo json_encode($datas);
					   }
					   else
					   {
						   $datas["status"] = "Fail";
					       $datas["message"] = "Sorry ! . Something wrong Please try again .";
					       echo json_encode($datas);
					   }
				   }
		}
		/////////////////////  accept end /////////////////////////////////////////////
		
		/////////////////////  delete ////////////////////////////////////////////
		
		if(isset($_POST["delete"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $check = $this->Webservice->checkinvitenumber($_POST["eventid"]);
					   if($check)
					   {
						  if($check > 3)
						  {
							  $datas["status"] = "Fail";
					          $datas["message"] = "Sorry ! . You are not able to delete this event . Because more than 3 members are invited on this event  .";
					          echo json_encode($datas);
						  }
						  else
						  {
							  $deleteevent = $this->Webservice->deleteevent($_POST["eventid"]);
							  
							  $deleteinvite = $this->Webservice->deleteinvite($_POST["eventid"]);
							  
							  if($deleteevent && $deleteinvite)
							  {
								  $datas["status"] = "success";
					              $datas["message"] = "Event has been deleted successfully .";
					              echo json_encode($datas);
							  }
							  else
							  {
								 $datas["status"] = "Fail";
					             $datas["message"] = "Sorry ! . Something wrong please try again.";
					             echo json_encode($datas); 
							  }
						  }
                         
						  
					   }
					   else
					   {
						   $deleteevent = $this->Webservice->deleteevent($_POST["eventid"]);
							  
							 // $deleteinvite = $this->Webservice->deleteinvite($_POST["eventid"]);
							  
							  if($deleteevent)
							  {
								  $datas["status"] = "success";
					              $datas["message"] = "Event has been deleted successfully .";
					              echo json_encode($datas);
							  }
							  else
							  {
								 $datas["status"] = "Fail";
					             $datas["message"] = "Sorry ! . Something wrong please try again.";
					             echo json_encode($datas); 
							  }
					   }
				   }
		}
		//////////////////////  delete end /////////////////////////////////////////////
		
		//////////////////////  message /////////////////////////////////////////////
		
		if(isset($_POST["message"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('msg', 'Message', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $data["eventid"] = $_POST["eventid"];
					   $data["userid"] = $_POST["userid"];
					   $data["message"] = $_POST["msg"];
					   $data["type"] = "chat";
					  // $data["created"] = date(" m/d/Y h:s A");
					   $data["created"] = $_POST["date"];
					   
					   $selectdeletedgrp = $this->Webservice->selectdeletedgrp($_POST["eventid"]);
					   if($selectdeletedgrp)
					   {
						   foreach($selectdeletedgrp as $deletelist)
						   {
							   $this->Webservice->deletedeletedgrp($deletelist->id);
						   }
				       }
					   
					    
					   $insert = $this->Webservice->insetchat($data);
					   if($insert)
					   {
						   
						   $selectcreate = $this->Webservice->selectevent($_POST["eventid"]);
						   if($selectcreate)
						   {
							  foreach($selectcreate as $userlist)
								{
									if($userlist->userid != $_POST["userid"])
									{
										
										if($userlist->devicetype == "A")
										{
											 $id = array($userlist->deviceid);
												$message = array
											  (
												'eventid' => $_POST["eventid"],
												'message' => "You have a new message."
											   );
											  $push = $this->notification->send($id , $message);
											  //print_r($push);
										}
										else
										{
											$deviceToken =$userlist->deviceid;
											/*$message = array
											  (
												'identity_type' => 4,
												'message' => "CUSTOMER PROVIDE YOU RATING"
											   ); 
											   */
											 $message_data = $_POST["eventid"];  
											 $message = "You have a new message."; 
										  $passphrase =123456;
										  $ctx = stream_context_create();
										  stream_context_set_option($ctx, 'ssl', 'local_cert','./assets/push/pushcert.pem');
										  stream_context_set_option($ctx, 'ssl', 'passphrase', $passphrase);

										  $fp = stream_socket_client('ssl://gateway.sandbox.push.apple.com:2195', 
											$err, 
											$errstr, 
											60, 
											STREAM_CLIENT_CONNECT|STREAM_CLIENT_PERSISTENT, 
											$ctx);

										// Create the payload body
										$body['aps'] = array(
											'badge' => 0,
											'alert' => $message,
											'sound' => 'default',
											'data' => $message_data
										);

										$payload = json_encode($body);

										// Build the binary notification
										$msg = chr(0) . pack('n', 32) . pack('H*', $deviceToken) . pack('n', strlen($payload)) . $payload;

										// Send it to the server
										$result = fwrite($fp, $msg, strlen($msg));

										// Close the connection to the server
										fclose($fp);
										}
										
										
										
									}
								}									
						   }
						   
						   
						   
						   
						   
						   $selectalluser = $this->Webservice->alldevice($_POST["eventid"],$_POST["userid"]);
						   //print_r($selectalluser); die();
						   if($selectalluser)
						   {
							   
							   foreach($selectalluser as $list)
								{
									//print_r($list); die();
									if($list->devicetype == "A")
									{
										 $id = array($list->deviceid);
											$message = array
										  (
											'eventid' => $_POST["eventid"],
											'message' => "You have a new message."
										   );
										  $push = $this->notification->send($id , $message);
										  //print_r($push);
									}
									else
									{
										$deviceToken =$list->deviceid;
										/*$message = array
										  (
											'identity_type' => 4,
											'message' => "CUSTOMER PROVIDE YOU RATING"
										   ); 
										   */
										 $message_data = $_POST["eventid"];  
										 $message = "You have a new message."; 
									  $passphrase =123456;
									  $ctx = stream_context_create();
									  stream_context_set_option($ctx, 'ssl', 'local_cert','./assets/push/pushcert.pem');
									  stream_context_set_option($ctx, 'ssl', 'passphrase', $passphrase);

									  $fp = stream_socket_client('ssl://gateway.sandbox.push.apple.com:2195', 
										$err, 
										$errstr, 
										60, 
										STREAM_CLIENT_CONNECT|STREAM_CLIENT_PERSISTENT, 
										$ctx);

									// Create the payload body
									$body['aps'] = array(
										'badge' => 0,
										'alert' => $message,
										'sound' => 'default',
										'data' => $message_data
									);

									$payload = json_encode($body);

									// Build the binary notification
									$msg = chr(0) . pack('n', 32) . pack('H*', $deviceToken) . pack('n', strlen($payload)) . $payload;

									// Send it to the server
									$result = fwrite($fp, $msg, strlen($msg));

									// Close the connection to the server
									fclose($fp);
									}
									
								}
							   
						   }
						   
						   
						   
						   
						   $updatedata["updatedtime"] = date('y/m/d h:i:s a');
						   $this->Webservice->updateevent($_POST["eventid"] , $updatedata);
						   
						   $datas["status"] = "success";
					       $datas["message"] = "chat has been inserted successfully";
					       echo json_encode($datas); 
					   }
					   else
					   {
						   $datas["status"] = "Fail";
					       $datas["message"] = "Sorry ! . Something wrong please try again.";
					       echo json_encode($datas); 
					   }
				   }
		}
		
		//////////////////////  message end /////////////////////////////////////////////
		
		//////////////////////  messagelast  /////////////////////////////////////////////
		if(isset($_POST["messagelast"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $select = $this->Webservice->selectlastmessage($_POST["userid"],$_POST["eventid"]);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							 $datas["message"][] = $list;   
						   }
						    echo json_encode($datas);
					   }
					   else
					   {
						  $datas["status"] = "Fail";
					      $datas["message"] = "Sorry ! no message found";
					      echo json_encode($datas); 
					   }
				   }
		}
		
		//////////////////////  messagelast end /////////////////////////////////////////////
		
		//////////////////////  messageall  /////////////////////////////////////////////
		if(isset($_POST["messageall"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $select = $this->Webservice->selectallmessage($_POST["userid"],$_POST["eventid"]);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							$alldata[] = array
							(
							 "chatid" => $list->chatid,
							 "user_id" => $list->user_id,
							 "eventid" => $list->eventid,
							 "message" => $list->message,
							 "created" => $list->created,
							 "type" => $list->type,
							 "fname" => $list->fname,
							 "lname" => $list->lname,
							 "topicname" => $list->topicname,
							 "event_created" => $list->event_created,
							 "read" => $this->Webservice->readornot($list->chatid,$_POST["userid"],$_POST["eventid"])
							);
							
							  
						   }
						     $datas["message"] = $alldata;
						     echo json_encode($datas);
					   }
					   else
					   {
						  $datas["status"] = "Fail";
					      $datas["message"] = "Sorry ! no message found";
					      echo json_encode($datas); 
					   }
				   }
		}
		
		//////////////////////  messageall end /////////////////////////////////////////////
		
		//////////////////////  read           /////////////////////////////////////////////
		if(isset($_POST["read"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('chatid', 'Chat Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $data["chatid"] = $_POST["chatid"];
					   $data["userid"] = $_POST["userid"];
					   $data["eventid"] = $_POST["eventid"];
					   
					   $select = $this->Webservice->checkinsertchatstatus($_POST["eventid"],$_POST["userid"],$_POST["chatid"]);
					   if($select)
					   {
						      $datas["status"] = "success";
							  $datas["message"] = "Data has been Updated successfully";
							  echo json_encode($datas);	   
					   }
					   else
					   {
						   $insert = $this->Webservice->insertchatstatus($data);
						   if($insert)
						   {
							  $datas["status"] = "success";
							  $datas["message"] = "Data has been Updated successfully";
							  echo json_encode($datas);	
						   }
						   else
						   {
							   $datas["status"] = "Fail";
							   $datas["message"] = "Something wrong . We are not able to insert this query . Please try again";
							  echo json_encode($datas);	
						   } 
					   }
					   
				   }
		}
		////////////////////// read end /////////////////////////////////////////////
		
		
		
		
		//////////////////////  deletemessage   /////////////////////////////////////////////
		if(isset($_POST["deletemessage"]))
		{
			        $this->load->library('form_validation');
				    //$this->form_validation->set_rules('chatid', 'Chat Id', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   
					    $delete = $this->Webservice->deletemsg($_POST["eventid"],$_POST["userid"]);
						if($delete)
						{
						  $datas["status"] = "success";
					      $datas["message"] = "Message has been Deleted successfully";
					      echo json_encode($datas);	
						}
						else
						{
						  $datas["status"] = "Fail";
					      $datas["message"] = "Something wrong . We are not able to delete this message . Please try again";
					      echo json_encode($datas);	
						}
				   }
		}
		//////////////////////  deletemessage end /////////////////////////////////////////////
		
		
		
		
		
	   //////////////////////  HotTopics  /////////////////////////////////////////////
		 if(isset($_POST["HotTopics"]))
		 {
			           $join = array();
			           $joinid = $this->Webservice->alreadyjoin($_POST["userid"]);
					   if($joinid)
					   {
						 foreach($joinid as $joindata)
					   {
						   array_push($join, $joindata->eventid); 
					   }

                        $this->Webservice->deleteafter3hours($_POST["time_zone"]);
			           $select = $this->Webservice->hottopics($_POST["userid"] , $join);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							  $date = new DateTime($list->starttime);
                               $newdate = $date->format('m/d/Y H:i A');
							  
							  $datas["message"][] = array
							  (
							   "event_id" => $list->event_id,
							   "user_id" => $list->user_id,
							   "Course_Name" => $list->Course_Name,
							   "topic" => $list->topic,
							   "starttime" => $newdate,
							   "location" => $list->location,
							   "participants" => $this->Webservice->joincount($list->event_id),
							   "fname" => $list->fname,
							   "lname" => $list->lname,
							   "userpic" => $list->userpic,
							   "eventdate" => $list->eventdate
							  ); 
						   }
						  
						   echo json_encode($datas);  
					   }
					   else
					   {
						 $datas["status"] = "Fail";
					     $datas["message"] = "Sorry ! . Event list not found .";
					     echo json_encode($datas);  
					   }					   
					   }
					   else
					   {
						 $datas["status"] = "Fail";
					     $datas["message"] = "Sorry ! . Event list not found .";
					     echo json_encode($datas); 
					   }
					   
					   /*print_r($join);
					   die();*/
					   
		 }
		
		//////////////////////  HotTopics end /////////////////////////////////////////////
		
		//////////////////////  Join  /////////////////////////////////////////////
		
		if(isset($_POST["join"]))
		{
			        $this->load->library('form_validation');
				    $this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('friendsid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $data["userid"] = $_POST["userid"];
					   $data["eventid"] = $_POST["eventid"];
					   $data["inviteid"] = $_POST["friendsid"];
					   $data["status"] = 1;
					$check = $this->Webservice->selectjoinevent($_POST["friendsid"] ,$_POST["eventid"]);
					if($check)
					{
						$updatedata["status"] = 1;
						$update = $this->Webservice->updatejoinevent($_POST["friendsid"] ,$_POST["eventid"],$updatedata);
					   if($update)
					   {
						   
						    $insertdata["eventid"] = $_POST["eventid"];
					        $insertdata["userid"] = $_POST["friendsid"];
					        $insertdata["message"] = "";
					        $insertdata["type"] = "join";
					        $insertdata["created"] = date(" m/d/Y h:s A");
					       // $data["created"] = $_POST["date"];

					       $this->Webservice->insetchat($insertdata);
						   
						   
						   
						   $updatedata["updatedtime"] = date('y/m/d h:i:s a');
						   $this->Webservice->updateevent($_POST["eventid"] , $updatedata);
						   
						   $datas["status"] = "success";
					       $datas["message"] = "You have join this event successfully .";
					       echo json_encode($datas);
					   }
					   else
					   {
						   $datas["status"] = "Fail";
					       $datas["message"] = "Something wrong in insertion . Please try again .";
					       echo json_encode($datas);
					   }
					}
					else
					{
					   $insert = $this->Webservice->joinevent($data);
					   if($insert)
					   {
						    $insertdata["eventid"] = $_POST["eventid"];
					        $insertdata["userid"] = $_POST["friendsid"];
					        $insertdata["message"] = "";
					        $insertdata["type"] = "join";
					        $insertdata["created"] = date(" m/d/Y h:s A");
					       // $data["created"] = $_POST["date"];

					        $this->Webservice->insetchat($insertdata);
						   
						   
						   
						   
						   $updatedata["updatedtime"] = date('y/m/d h:i:s a');
						   $this->Webservice->updateevent($_POST["eventid"] , $updatedata);
						   
						   $datas["status"] = "success";
					       $datas["message"] = "You have join this event successfully .";
					       echo json_encode($datas);
					   }
					   else
					   {
						   $datas["status"] = "Fail";
					       $datas["message"] = "Something wrong in insertion . Please try again .";
					       echo json_encode($datas);
					   }
					}
					   
				   }
			
		}
		
		//////////////////////  Join  End /////////////////////////////////////////////
		
		//////////////////////  invitecount   /////////////////////////////////////////
		
		if(isset($_POST["invitecount"]))
		{
			        $this->load->library('form_validation');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $select = $this->Webservice->invitecount($_POST["userid"]);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							 $datas["message"][] = $list;  
						   }
						   echo json_encode($datas);
					   }
					   else
					   {
						   $datas["status"] = "Fail";
					       $datas["message"] = "Sorry . No invite found";
					       echo json_encode($datas);
					   }
				   }
		}
		
		//////////////////////  invitecount  End /////////////////////////////////////////////
		
		//////////////////////  joineventlist    /////////////////////////////////////////////
		if(isset($_POST["joineventlist"]))
		{
			        $this->load->library('form_validation');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					   $this->Webservice->deleteafter3hours($_POST["time_zone"]);
					   $select = $this->Webservice->joineventlist($_POST["userid"]);
					   if($select)
					   {
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							   $date = new DateTime($list->starttime);
                               $newdate = $date->format('m/d/Y H:i A');
							   
							 $datas["message"][] = array
							 (
							    "event_id" => $list->event_id,
							    "userid" => $list->userid,
								"Course_Name" => $list->Course_Name,
								"topic" => $list->topic,
								"starttime" => $newdate,
								"location" => $list->location,
								"participants" => $this->Webservice->joincount($list->event_id),
								"fname" => $list->fname,
								"lname" => $list->lname,
								"userpic" => $list->userpic,
								"eventdate" => $list->eventdate
							 );  
						   }
						   echo json_encode($datas);
					   }
					   else
					   {
						   
						   $datas["status"] = "Fail";
					       $datas["message"] = "Sorry . No invite found";
					       echo json_encode($datas); 
					   }
					   
				   }
		}
		//////////////////////  joineventlist  End /////////////////////////////////////////////
		
		
		//////////////////////  profileedit         ///////////////////////////////////////////
		
		if(isset($_POST["profileedit"]))
		{
			        $this->load->library('form_validation');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				   else
				   {
					  // print_r($_FILES["image"]); die();
					   
					   /*if(!empty($_POST["fname"]))
					   {
						  $data["fname"] = $_POST["fname"];
					   }
					   
					   if(!empty($_POST["lname"]))
					   {
						  $data["lname"] = $_POST["lname"]; 
					   }*/
					   
					   if((!empty($_FILES["image"])) && ($_FILES['image']['error'] == 0))
					   {
						$name = $_FILES["image"]["name"];
						$config['upload_path'] = realpath(APPPATH . '../assets/user');
						$config['allowed_types'] = 'gif|jpg|png|jpeg';
						$config['max_size'] = '2000000';
						$config['remove_spaces'] = true;
						$config['overwrite'] = false;
						$config['encrypt_name'] = true;
						$config['max_width']  = '';
						$config['max_height']  = '';
						$this->load->library('upload', $config);
						$this->upload->initialize($config);            
						if ($this->upload->do_upload('image'))
						{
						  $image_data = $this->upload->data();
						  $data["userpic"] = $image_data["file_name"];
 
						}
						else
						{
							
							$datas["status"] = "Fail";
					       $datas["message"] = $this->upload->display_errors();
					       echo json_encode($datas);
						}	
					   }
					   
					   $update = $this->Webservice->updateprofile($_POST["userid"] , $data);
					   if($update)
					   {
						   $select = $this->Webservice->selectuserinfo($_POST["userid"]);
						   $datas["status"] = "success";
						   foreach($select as $list)
						   {
							 $datas["message"] = $list;  
						   }

					       echo json_encode($datas);
					   }
					   else
					   {
						   $datas["status"] = "Fail";
					       $datas["message"] = "Sorry something wrong in updation please try again . ";
					       echo json_encode($datas);
					   }
				   }
		}
		
		////////////////////////  profileedit  End //////////////////////////////////////////////
		
		
		///////////////////////  participantslist  //////////////////////////////////////////////
		
		if(isset($_POST["participantslist"]))
		{
			
			        $this->load->library('form_validation');
					$this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
				    else
				    {
						
						$creater = $this->Webservice->eventcreater($_POST["eventid"]);
						//print_r($creater); die(); 
					    if($creater)
						{
							foreach($creater as $createrlist)
							{
								$createrdata = $createrlist;
							}							
						}							
						$select = $this->Webservice->participantslist($_POST["eventid"]);
						if($select)
						{
							
							$datas["status"] = "success";
							foreach($select as $list)
							{
								 $datas["message"][] = $list;
							}
							//$datas["eventcreater"] = $createrdata;
							echo json_encode($datas);
						}
						else
						{
							 $datas["status"] = "Fail";
					         $datas["message"] = "Sorry List not found";
					         echo json_encode($datas);
						}
				    }
		}
		
		/////////////////////  participantslist  End /////////////////////////////////////////////
	    
		if(isset($_POST["reject"]))
		{
			        $this->load->library('form_validation');
					$this->form_validation->set_rules('userid', 'User Id', 'trim|required');
					$this->form_validation->set_rules('eventid', 'Event Id', 'trim|required');
					if($this->form_validation->run() == FALSE)
					{
					   $datas["status"] = "Fail";
					   $datas["message"] = "All Fields Are Required";
					   echo json_encode($datas);
					}
					else
					{
						$reject = $this->Webservice->reject($_POST["userid"] , $_POST["eventid"]);
						if($reject)
						{
							$datas["status"] = "success";
					        $datas["message"] = "Event has been rejected successfully .";
					        echo json_encode($datas);
						}
						else
						{
							$datas["status"] = "Fail";
					        $datas["message"] = "Sorry something wrong . Please try again .";
					        echo json_encode($datas);
						}
					}
			
		}
		
		}
		else
		{
			$this->load->view('Webservices');
		}
		
	}

}

