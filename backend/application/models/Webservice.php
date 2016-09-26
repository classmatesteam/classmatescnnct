<?php
Class Webservice extends CI_Model
{

    function User_register($data)
	{
		return $this->db->insert('user',$data);
	}

    function already_register_social($loginid)
	{
		   $this -> db -> select('*');
		   $this -> db -> from('user');
		   $this -> db -> where('loginid', $loginid);
		   $this -> db -> limit(1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() == 1)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
		
	}
	
	function deleteafter3hours($time_zone)
	{
		date_default_timezone_set($time_zone);
		
        $now = date('Y-m-d h:i:s');

		$where = "TIMESTAMPDIFF(HOUR,'".$now."',starttime) <= -5";
		$this -> db -> where($where);
		return $this -> db -> delete('event');
		
	}
	
	function updateprofile($userid , $data)
	{
		$this->db->where('user.userid', $userid);
		//$this->db->where('user.name', $name);
		return $this->db->update('user', $data);
	}
	function selectuserinfo($userid)
	{
		   $this -> db -> select('fname , lname , userpic');
		   $this -> db -> from('user');
		   $this -> db -> where('userid', $userid);
		   $this -> db -> limit(1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() == 1)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	}
	function selectclass($classid)
	{
		   $this -> db -> select('*');
		   $this -> db -> from('class');
		   $this -> db -> where('classid', $classid);
		   //$this -> db -> where('name', $name);
		   $this -> db -> limit(1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	}
	
	function eventupdate($userid , $name , $data)
	{
		$this->db->where('event.user_id', $userid);
		$this->db->where('event.name', $name);
		return $this->db->update('event', $data);
	}
	
	function joinevent($data)
	{
		return $this->db->insert('invite',$data);
	}
	
	function updatejoinevent($inviteid ,$eventid , $data)
	{
		$this->db->where('invite.inviteid', $inviteid);
		$this->db->where('invite.eventid', $eventid);
		return $this->db->update('invite', $data);
	}
	
	
	function selectjoinevent($inviteid ,$eventid)
	{
		   $this -> db -> select('*');
		   $this -> db -> from('invite');
		   $this -> db -> where('inviteid', $inviteid);
		   $this -> db -> where('eventid', $eventid);
		   $this -> db -> where('status', 0);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			   return $query->result();
		   }
		   else
		   {
			   
			 return false;
		   }
	}
	function joincount($eventid)
	{
		   $this -> db -> select('count(*) as total');
		   $this -> db -> from('invite');
		   $this -> db -> where('eventid', $eventid);
		   $this -> db -> where('status', 1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			   foreach($query->result() as $data)
			   {
				  return $data->total + 1;
			   }
			 
		   }
		   else
		   {
			   
			 return 1;
		   }
		
	}
	
	function eventcreater($eventid)
	{
		   $this -> db -> select('user.fname as fname , user.lname as lname , user.userpic as userpic');
		   $this -> db -> from('event');
		   $this -> db -> join('user','user.userid = event.user_id');
		   $this -> db -> where('event.eventid', $eventid); 
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			   
			  return $query->result();
		
		   }
		   else
		   {
			   
			 return false;
		   }
		
	}
	
	function participantslist($eventid)
	{     
	       $allid = array();
		
		
		   $this -> db -> distinct('invite.inviteid');
		   $this -> db -> select('invite.inviteid as inviteid');
		   $this -> db -> from('invite');
		   $this -> db -> join('user','user.userid = invite.inviteid');
		   $this -> db -> where('invite.eventid', $eventid); 
		   $this -> db -> where('invite.status', 1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			   
			  foreach($query->result() as $list)
			  {
				  array_push($allid , $list->inviteid);
			  }
		
		   }
		   else
		   {
			   array_push($allid , 0);
		   }
		
		
		
	 	  
		   $this -> db -> select('user_id');
		   $this -> db -> from('event');
		   $this -> db -> where('eventid', $eventid); 
	       $query1 = $this -> db -> get();
		   if($query1 -> num_rows() > 0)
		   {
			   
			  foreach($query1->result() as $list1)
			  {
				  array_push($allid , $list1->user_id);
			  }
		
		   }
		   else
		   {
			   array_push($allid , 0);
		   }
           		
		
		
		
		
		  // $this -> db -> distinct('invite.inviteid');
		   $this -> db -> select('fname ,lname ,userpic');
		   $this -> db -> from('user');
		   //$this -> db -> join('user','user.userid = invite.inviteid');
		   $this -> db -> where_in('userid', $allid); 
		   //$this -> db -> where('invite.status', 1);
	       $query2 = $this -> db -> get();
		   if($query2 -> num_rows() > 0)
		   {
			   
			  return  $query2->result();
		
		   }
		   else
		   {
  
				 return false;
		   }
	}
	
	function joineventlist($id)
	{
		   $this -> db -> select('event.eventid as event_id , event.user_id as userid , event.name as Course_Name , event.topicname as topic , event.starttime as starttime ,event.created as eventdate, event.location as location , event.participants as participants , user.fname as fname , user.lname as lname , user.userpic as userpic');
		   $this -> db -> from('invite');
		   $this -> db -> join('event','event.eventid = invite.eventid AND event.user_id != '.$id.'');
		   $this -> db -> join('user','user.userid = invite.userid');
		   $this -> db -> where('invite.inviteid', $id);
		   $this -> db -> where('invite.status', 1);
		   $this -> db -> order_by('invite.joindate','desc');
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			   
			 return false;
		   } 
		
	}
	
	function invitecount($id)
	{
		   //$this -> db -> distinct('invite.eventid');
		   $this -> db -> select('event.eventid as event_id , event.name ,event.user_id as createduser , event.topicname , invite.userid as userid , invite.id as inviteid , user.fname as fname , user.lname as lname , user.userpic as userpic ');
		   $this -> db -> from('invite');
		   $this -> db -> join('event','event.eventid = invite.eventid');
		   $this -> db -> join('user','user.userid = invite.userid');
		   $this -> db -> where('invite.inviteid', $id);
		   $this -> db -> where('invite.status', 0);
		   //$this->db->group_by('invite.eventid');
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	}
	
	function updatelogincount($loginid , $data)
	{
		$this->db->where('user.loginid', $loginid);
		return $this->db->update('user', $data);
	}
	
	function addclass($data)
	{
	  return $this->db->insert('class',$data);	
	}
	
	function checkclassname($userid , $name)
	{
		   $this -> db -> select('*');
		   $this -> db -> from('class');
		   $this -> db -> where('user_id', $userid);
		   $this -> db -> where('name', $name);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	}
	
	function checkcourseandtopicname($userid , $tname)
	{
		   $this -> db -> select('*');
		   $this -> db -> from('event');
		   $this -> db -> where('user_id', $userid);
		   //$this -> db -> where('name', $name);
		   $this -> db -> where('topicname', $tname);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	}
	
	function editclass($classid , $data)
	{
		$this->db->where('class.classid', $classid);
		return $this->db->update('class', $data);
	}
	
	function addevent($data)
	{
	  return $this->db->insert('event',$data);	
	}
	
    function searchevent($name)
	{
		   $this -> db -> select('event.eventid as event_id ,event.user_id as userid , event.name as Course_Name , event.topicname as topic , event.starttime as starttime , event.created as eventdate , event.location as location , event.participants as participants , user.fname as fname , user.lname as lname , user.userpic as userpic');
		   $this -> db -> from('event');
		   $this -> db -> join('user','user.userid = event.user_id');
		   $this -> db -> where('event.name', $name);
		   $this -> db -> order_by('updatedtime','desc');
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   } 
	}
	function removeclass($classid , $userid)
	{
		   $this -> db -> select('*');
		   $this -> db -> from('class');
		   $this -> db -> where('classid', $classid);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 foreach($query->result() as $list)
			 {
				$this-> db -> where('event.name',$list->name);
		        $this-> db -> where('event.user_id',$userid);
		        $this -> db ->delete('event'); 
				 
			 }
		   }
		
		$this-> db -> where('class.classid',$classid);
		$this-> db -> where('class.user_id',$userid);
		return $this -> db ->delete('class');
	}	
	
	   function userclasslist($userid)
	   {
		   $this -> db -> select('*');
		   $this -> db -> from('class');
		   $this -> db -> where('user_id', $userid);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	   }
	   
	   function checkeventjoin($userid , $eventid)
	   {
		   $this -> db -> select('*');
		   $this -> db -> from('invite');
		   $this -> db -> where('inviteid', $userid);
		   $this -> db -> where('eventid',$eventid);
		   $this -> db -> where('status',1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   } 
	   }
	   
	   function usereventlist($userid)
	   {
		   $this -> db -> select('*');
		   $this -> db -> from('event');
		   $this -> db -> where('user_id', $userid);
		   $this -> db -> order_by('eventid','desc');
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }
	   }
	   
	   function updateevent($id , $data)
	   {
		   $this->db->where('event.eventid', $id);
		   return $this->db->update('event', $data);
	   }
	   
	   function grouplist($eventid , $userid)
	   {
		   $this -> db -> select('user.fname,user.lname,user.userpic,event.name,event.eventid,event.topicname');
		   $this -> db -> from('event');
		   $this -> db -> join('user','user.userid = event.user_id');
		   $this -> db -> where_in('event.eventid', $eventid);
		   $this -> db -> or_where('event.user_id', $userid);
		   $this -> db -> order_by('event.updatedtime','desc');
	       $query = $this -> db -> get();
		   //return $this->db->last_query(); 
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }  
	   }
	   
	   function groupcheck($eventid , $userid)
	   {
		   $this -> db -> select('*');
		   $this -> db -> from('deletedgrp');
		   $this -> db -> where('userid', $userid);
		   $this -> db -> where('eventid', $eventid);
	       $query = $this -> db -> get(); 
		   if($query -> num_rows() > 0)
		   {
			 return "No";
		   }
		   else
		   {
			 return "Yes";
		   }  
	   }
	   
	   function lastmsgtime($id)
	   {
		   $this -> db -> select('created');
		   $this -> db -> from('chat');
		   $this -> db -> where('eventid', $id);
		   $this -> db -> limit(1);
		   $this -> db -> order_by('chatid','desc');
	       $query = $this -> db -> get(); 
		   if($query -> num_rows() > 0)
		   {
			 foreach($query->result() as $data)
			 {
				 return $data->created;
			 }
		   }
		   else
		   {
			 return "NULL";
		   }   
	   }
	   
	   function lastmsg($id)
	   {
		   $this -> db -> select('message');
		   $this -> db -> from('chat');
		   $this -> db -> where('eventid', $id);
		   $this -> db -> limit(1);
		   $this -> db -> order_by('chatid','desc');
	       $query = $this -> db -> get(); 
		   if($query -> num_rows() > 0)
		   {
			 foreach($query->result() as $data)
			 {
				 return $data->message;
			 }
		   }
		   else
		   {
			 return "NULL";
		   }   
	   }
	   
	   function lastmsgid($id)
	   {
		   $this -> db -> select('chatid');
		   $this -> db -> from('chat');
		   $this -> db -> where('eventid', $id);
		   $this -> db -> limit(1);
		   $this -> db -> order_by('chatid','desc');
	       $query = $this -> db -> get(); 
		   if($query -> num_rows() > 0)
		   {
			 foreach($query->result() as $data)
			 {
				 return $data->chatid;
			 }
		   }
		   else
		   {
			 return "NULL";
		   }     
	   }
	   function contactlist($id , $name)
	   {
		   $this -> db -> select('user.userid as userid,user.fname as fname , user.lname as lname , user.userpic as userpic');
		   $this -> db -> from('class');
		   $this -> db -> join('user','user.userid = class.user_id');
		   $this -> db -> where('class.name', $name);
		   $this -> db -> where('class.user_id !=', $id);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }    
	   }
	   
	   function inviteornot($userid , $friendsid , $eventid)
	   {
		   $this -> db -> select('*');
		   $this -> db -> from('invite');
		   $this -> db -> where('userid', $userid);
		   $this -> db -> where('inviteid', $friendsid);
		   $this -> db -> where('eventid ', $eventid);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return "Yes";
		   }
		   else
		   {
			 return "No";
		   }     
	   }
	   
	   function invite($data)
	   {
		 return $this->db->insert('invite',$data);  
	   }
	   
	   function updateinvite($id , $friendsid , $data)
	   {
		   $this-> db -> where('invite.eventid',$id);
		   $this-> db -> where('invite.inviteid',$friendsid);
		   return $this -> db ->update('invite',$data);
	   }
	   
	   function checkinvitenumber($eventid)
	   {
		   
		   $this -> db -> select('*');
		   $this -> db -> from('invite');
		   $this -> db -> where('eventid', $eventid);
		  // $this -> db -> where('status', 1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->num_rows();
		   }
		   else
		   {
			 return false;
		   }   

		 
	   }
	   
	   function deleteevent($eventid)
	   {
		 $this-> db -> where('event.eventid',$eventid);
		 return $this -> db ->delete('event');  
	   }
	   
	   function deleteinvite($eventid)
	   {
		 $this-> db -> where('invite.status', 1);
		 $this-> db -> where('invite.eventid',$eventid);
		 return $this -> db ->delete('invite');
	   }
	   
	   function selectidfrominvite($inviteid)
	   {
		   $this -> db -> select('*');
		   $this -> db -> from('invite');
		   $this -> db -> where('inviteid', $inviteid);
		   $this -> db -> where('status', 1);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }     
	   }
	   
	   function alldevice($eventid , $userid)
	   {
		   $this -> db -> select('user.userid , user.devicetype , user.deviceid');
		   $this -> db -> from('invite');
		   $this -> db -> join('user','user.userid = invite.inviteid');
		   $this -> db -> where('invite.eventid', $eventid);
		   $this -> db -> where('invite.inviteid != ', $userid);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   } 
	   }
	   
	   function selectevent($eventid)
	   {
		   $this -> db -> select('user.userid , user.devicetype , user.deviceid');
		   $this -> db -> from('event');
		   $this -> db -> join('user','user.userid = event.user_id');
		   $this -> db -> where('event.eventid', $eventid);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   } 
	   }
	   
	   function insetchat($data)
	   {
		    return $this->db->insert('chat',$data);
	   }
	   
	   function selectlastmessage($userid , $eventid)
	   {
		   $this -> db -> select('chat.chatid , chat.userid as user_id, chat.eventid , chat.message , chat.created , user.fname , user.lname , event.topicname , event.created as event_created');
		   $this -> db -> from('chat');
		   $this -> db -> join('user','user.userid = chat.userid');
		   $this -> db -> join('event','event.eventid = chat.eventid');
		   $this -> db -> where('chat.userid', $userid);
		   $this -> db -> where('chat.eventid', $eventid);
		   $this -> db -> limit(1);
		   $this -> db -> order_by('chatid','desc');
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }       
	   }
	   
	   function selectallmessage($userid , $eventid)
	   {
		   $notid = array();
		   
		   $this -> db -> select('*');
		   $this -> db -> from('deletedmsg');
		   $this -> db -> where('userid', $userid);
		   $this -> db -> where('eventid', $eventid);
		   $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 foreach($query->result() as $list)
			 {
				 array_push($notid , $list->chatid);
			 }
		   }
		   else
		   {
			  $notid = 0; 
		   }
		   
		   
		   $this -> db -> select('chat.chatid , chat.userid as user_id, chat.eventid , chat.message , chat.created ,chat.type , user.fname , user.lname , event.topicname , event.created as event_created');
		   $this -> db -> from('chat');
		   $this -> db -> join('user','user.userid = chat.userid');
		   $this -> db -> join('event','event.eventid = chat.eventid');
		   //$this -> db -> where('chat.userid', $userid);
		   $this -> db -> where('chat.eventid', $eventid);
		   $this -> db -> where_not_in('chat.chatid', $notid);
		   $this -> db -> order_by('chatid','asc');
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			  // print_r($this -> db -> last_query());
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }    
	   }
	   
	   function insertchatstatus($data)
	   {
		   return $this->db->insert('chatstatus',$data); 
	   }
	   
	   function checkinsertchatstatus($eventid,$userid,$chatid)
	   {
		   $this -> db -> select('*');
		   $this -> db ->from('chatstatus');
		   $this -> db -> where('eventid',$eventid);
		   $this -> db -> where('userid',$userid);
		   $this -> db -> where('chatid',$chatid);		
           $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
             return $query->result();   			   
		   }
		   else
		   {
			return false;	
		   }	
	   }
	   function readornot($chatid,$userid,$eventid)
	   {
		   $this -> db -> select('*');
		   $this -> db ->from('chatstatus');
		   $this -> db -> where('eventid',$eventid);
		   $this -> db -> where('userid',$userid);
		   $this -> db -> where('chatid',$chatid);		
           $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
             return 1;   			   
		   }
		   else
		   {
			return 0;	
		   }	
		   
	   }
	   
	   function deletemsg($eventid , $userid)
	   {
		   $this -> db -> select('*');
		   $this -> db ->from('chat');
		   $this -> db -> where('eventid',$eventid);		
           $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 foreach($query->result() as $list)
			 {
				$insertdata["userid"] =  $userid;
				$insertdata["eventid"] =  $eventid;
				$insertdata["chatid"] =  $list->chatid;
			    $this -> db -> insert('deletedmsg',$insertdata);
			 }
		   }
		   
			  $insertdatas["userid"] = $userid;
		      $insertdatas["eventid"] = $eventid;
		   
		     return  $this -> db -> insert('deletedgrp',$insertdatas); 
		  
		   
		 		 
	   }
	   
	   function deletedeletedgrp($id)
	   {
		   $this -> db -> where('deletedgrp.id',$id);
		   return $this -> db -> delete('deletedgrp');
		   
	   }
	   
	   function selectdeletedgrp($eventid)
	   {
		   $this -> db -> select('*');
		   $this -> db ->from('deletedgrp');
		   $this -> db -> where('eventid',$eventid);		
           $query = $this -> db -> get();
           //$query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
             return $query->result();   			   
		   }
			else
			{
			return false;	
			}	
	   }
	   
	   function alreadyjoin($userid)
	   {
		$this -> db ->select('eventid');
        $this -> db -> from('invite');	
        $this -> db -> where('inviteid',$userid);
        $this -> db -> distinct('eventid');		
        $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   }    		
	   }
	   function hottopics($id , $joinid)
	   {
		   $allclass = array();
		   
		   $this -> db -> select('*');
		   $this -> db -> from('class');
		   $this -> db -> where('user_id', $id);
		   $query_one = $this -> db -> get();
		   if($query_one -> num_rows() > 0)
		   {
			 foreach($query_one->result() as $list)
			 {
				array_push($allclass , $list->name); 
			 }			 
		   }
		   else
		   {
			  $allclass = "classname"; 
		   }
		   
		   $this -> db -> select('event.eventid as event_id , event.user_id as user_id, event.name as Course_Name , event.topicname as topic , event.starttime as starttime ,event.created as eventdate, event.location as location , event.participants as participants , user.fname as fname , user.lname as lname , user.userpic as userpic');
		   $this -> db -> from('event');
		   $this -> db -> join('user','user.userid = event.user_id');
		  // $this -> db -> join('invite','invite.eventid != event.eventid AND  invite.inviteid ='.$id.' ');
		   $this -> db -> where_not_in('event.eventid ' , $joinid);
		   $this -> db -> where('event.user_id !=',$id);
		   $this -> db -> where_in('event.name',$allclass);
		   $this -> db -> order_by('event.participants','desc');
		   $this -> db -> order_by('event.created','desc');
		   $this -> db -> limit(5);
	       $query = $this -> db -> get();
		   if($query -> num_rows() > 0)
		   {
			  // print_r($this->db->last_query()); die();
			 return $query->result();
		   }
		   else
		   {
			 return false;
		   } 
	   }
	   function reject($userid , $eventid)
	   {
		   $this -> db -> where('invite.inviteid',$userid);
		   $this -> db -> where('invite.eventid',$eventid);
		   return $this -> db -> delete('invite');
		   
	   }

}




?>