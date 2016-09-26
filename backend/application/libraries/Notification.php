<?php  if (! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Name: Facebook Login Library
*
* Author: appleboy
*
*/
class Notification
{
    protected $ci;
    
    public function __construct()
    {
        $this->ci =& get_instance();
        $this->ci->config->load('notification');
    }

public function send($registatoin_ids , $message)
{
	 
	 $GOOGLE_API_KEY = $this->ci->config->item("API_ACCESS_KEY");
	// Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';
 
        $fields = array(
            'registration_ids' => $registatoin_ids,
            'data' => $message,
        );
 
        $headers = array(
            'Authorization: key=' . $GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        //print_r($headers);
        // Open connection
        $ch = curl_init();
 
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        // Execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
           // die('Curl failed: ' . curl_error($ch));
        }
 
        // Close connection
        curl_close($ch);
        // echo $result;
    }
	
}