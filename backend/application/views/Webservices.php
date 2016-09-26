______________________________________________________   facebook login       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="Fname" placeholder="First Name">
<input type="text" name="Lname" placeholder="Last Name">
<input type="text" name="Image" placeholder="Image">
<input type="text" name="access_token" placeholder="token">
<input type="text" name="DEVICE_ID" placeholder="DEVICE ID">
<input type="text" name="DEVICE_TYPE" placeholder="DEVICE TYPE">
<input type="submit" name="Fb_login" >
</form>

 
______________________________________________________   Login Via Uni       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="name" placeholder="Class Name">
<input type="text" name="pname" placeholder="Proffessor Name">
<input type="submit" name="unilogin" >
</form>


______________________________________________________   Add Class       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<!-- <input type="text" name="rollno" placeholder="Roll Number"> -->
<input type="text" name="name" placeholder="Class Name">
<input type="text" name="pname" placeholder="Proffessor Name">
<input type="submit" name="Addclass" >
</form>


______________________________________________________   Edit Class       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="classid" placeholder="Class Id">
<input type="text" name="name" placeholder="Class Name">
<input type="submit" name="Editclass" >
</form>

______________________________________________________   Add Event       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="name" placeholder="Course Name">
<input type="text" name="tname" placeholder="Topic Name">
<input type="text" name="location" placeholder="Location">
<input type="text" name="time" placeholder="Start Time">
<input type="text" name="time_zone" placeholder="Time zone">
<!-- <input type="text" name="participants" placeholder="Max No. Of Participants"> -->
<input type="submit" name="Addevent" >
</form>


______________________________________________________   Remove class       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="classid" placeholder="Class Id">
<input type="submit" name="removeclass" >
</form>

____________________________________________________  user class list       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="submit" name="classlist" >
</form>

____________________________________________________  user event list       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="time_zone" placeholder="Time zone">
<input type="submit" name="myeventlist" >
</form>

______________________________________________ search event user list       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="name" placeholder="Course Number">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="time_zone" placeholder="Time zone">
<input type="submit" name="searchevent" >
</form>

______________________________________________          Group list       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="submit" name="grouplist" >
</form>


______________________________________________    Group participants list       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="eventid" placeholder="Event Id">
<input type="submit" name="participantslist" >
</form> 
______________________________________________          contacts list       ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="name" placeholder="Course Name">
<input type="text" name="eventid" placeholder="Event Id">
<input type="submit" name="contactlist" >
</form>

______________________________________________          invite        ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="friendsid" placeholder="Friends Id">
<input type="submit" name="invite" >
</form>

_____________________________________________     accept invitation        ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="inviteid" placeholder="Invite Id">
<input type="text" name="friendsid" placeholder="Friends Id">
<input type="submit" name="accept" >
</form>

_____________________________________________     Delete event        ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="eventid" placeholder="Event Id">
<input type="submit" name="delete" >
</form>

_____________________________________________     insert chat     ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="date" placeholder="date">
<input type="text" name="msg" placeholder="Message">
<input type="submit" name="message" >
</form>

_____________________________________________     Get last message    ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="userid" placeholder="User Id">
<input type="submit" name="messagelast" >
</form>

_____________________________________________     Get all message    ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="userid" placeholder="User Id">
<input type="submit" name="messageall" >
</form>
_____________________________________________     Delete message    ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<!-- <input type="text" name="chatid" placeholder="Chat Id"> -->
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="userid" placeholder="User Id">
<input type="submit" name="deletemessage" >
</form>

_____________________________________________     Get Hot topics    ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="time_zone" placeholder="Time zone">
<input type="submit" name="HotTopics" >
</form>



_____________________________________________     Join Event   ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="friendsid" placeholder="Friends id">
<input type="submit" name="join">
</form>

_____________________________________________     Invite count details   ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="submit" name="invitecount">
</form>

_____________________________________________     Join Event List   ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="time_zone" placeholder="Time zone">
<input type="submit" name="joineventlist">
</form>

_____________________________________________     Edit Profile   ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>" enctype="multipart/form-data">
<input type="text" name="userid" placeholder="User Id">
<!-- <input type="text" name="fname" placeholder="first Name">
<input type="text" name="lname" placeholder="Last Name"> -->
<input type="file" name="image" placeholder="User Id">
<input type="submit" name="profileedit">
</form>


_____________________________________________     Reject   ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>" enctype="multipart/form-data">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="eventid" placeholder="Event Id">
<input type="submit" name="reject">
</form>

________________________________________  update read status   ________________________________________________________
<br>
&nbsp;
&nbsp;
&nbsp;
<form method="POST" action="<?php echo base_url('index.php/Webservices'); ?>" enctype="multipart/form-data">
<input type="text" name="userid" placeholder="User Id">
<input type="text" name="eventid" placeholder="Event Id">
<input type="text" name="chatid" placeholder="Chat Id">
<input type="submit" name="read">
</form>