//
//  ChatViewController.swift
//  Classmates
//
//  Created by MAC on 7/6/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import UIKit
import JSQMessagesViewController
import Alamofire
class ChatViewController: JSQMessagesViewController {
    
    var groupName = ""
    var createdTime = ""
    var userName = ""
    var eventID = ""
    var chatID = ""
    var messages = [JSQMessage]()
    //var sendMsg = [JSQMessage]()
    //var conversation: Conversation?
    var incomingBubble: JSQMessagesBubbleImage!
    var outgoingBubble: JSQMessagesBubbleImage!
    
    //var arrChatMessage = [String]()
    //var arrChatUserName = [String]()
    //var arrChatWriterID = [String]()
    
    var count = 0
    var timer: NSTimer = NSTimer()
    var onceOnly = false
    
    var date2 = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        configureDesign()
        setChat()
    }
    func configureDesign(){
        userID = defaults.stringForKey("userID")!
        eventID = defaults.stringForKey("eventID")!
        getAllMessagesApi()
        chatNext = false
        viewBack.backgroundColor = UIColor.clearColor()
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ChatViewController.actionBack)))
        imgParticipants.userInteractionEnabled = true
        imgParticipants.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ChatViewController.actionImage)))
        self.gpNameLabel.text = groupName
        self.timeLabel.text = createdTime
        let date = NSDate()
        //let calendar = NSCalendar.currentCalendar()
        // let components = calendar.components([.Day , .Month , .Year], fromDate: date)
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy HH:mm a"
        date2 = dateFormatter.stringFromDate(date)
    }
    func setChat(){
        senderId = userID
        senderDisplayName = "Me"
        self.collectionView?.backgroundColor = UIColor.clearColor()
        collectionView?.collectionViewLayout.incomingAvatarViewSize = .zero
        collectionView?.collectionViewLayout.outgoingAvatarViewSize = .zero
        
        incomingBubble = JSQMessagesBubbleImageFactory(bubbleImage: UIImage(named: "chat_right")!, capInsets: UIEdgeInsetsZero, layoutDirection: UIApplication.sharedApplication().userInterfaceLayoutDirection).incomingMessagesBubbleImageWithColor(colorPink)
        outgoingBubble = JSQMessagesBubbleImageFactory(bubbleImage: UIImage(named: "chat_right")!, capInsets: UIEdgeInsetsZero, layoutDirection: UIApplication.sharedApplication().userInterfaceLayoutDirection).outgoingMessagesBubbleImageWithColor(colorPink)
        self.inputToolbar.setBackgroundImage(UIImage(),
                                             forToolbarPosition: UIBarPosition.Any,
                                             barMetrics: UIBarMetrics.Default)
        self.inputToolbar.setShadowImage(UIImage(),
                                         forToolbarPosition: UIBarPosition.Any)
        collectionView?.collectionViewLayout.springinessEnabled = false
        automaticallyScrollsToMostRecentMessage = true
        //self.collectionView?.reloadData()
        self.collectionView?.layoutIfNeeded()
        //timer = NSTimer.scheduledTimerWithTimeInterval(5.0, target: self, selector: #selector(ChatViewController.getAllMessagesApi), userInfo: nil, repeats: true)
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        shouldShow = false;
        timer = NSTimer.scheduledTimerWithTimeInterval(3.0, target: self, selector: #selector(ChatViewController.getAllMessagesApi), userInfo: nil, repeats: true)
    }
    
    // MARK: JSQMessagesViewController method overrides
    override func didPressSendButton(button: UIButton, withMessageText text: String, senderId: String, senderDisplayName: String, date: NSDate) {
        
        NSOperationQueue.mainQueue().addOperationWithBlock({() -> Void in
            self.callInsertChatApi(text)
        })
        
        self.messages.append(JSQMessage(senderId: senderId, senderDisplayName: senderDisplayName, date: date, text: text))
        self.finishSendingMessageAnimated(true)
    }
    func callInsertChatApi(text: String){
        /*print(text)
        let data = [
            "userid" : userID,
            "eventid" : eventID,
            "msg": text,
            "message" : ""
        ]
        print(data)*/
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "eventid" : eventID,
            "date" : date2,
            "msg": text,
            "message" : ""
            ])
            .responseJSON {response in
                //print(response.response)
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    if self.chatID != "" {
                    self.chatID = String(Int(self.chatID)! + 1)
                    NSOperationQueue.mainQueue().addOperationWithBlock({() -> Void in
                        self.readStatusApi(self.chatID)
                    })
                }
                    //self.messages.append(self.sendMsg[0])
                    //self.finishSendingMessageAnimated(true)
                    if let JSON = response.result.value {
                       // print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let message = JSON["message"] as! String
                            print(message)
                           // self.collectionView.reloadData()
                        }else{
                            self.simpleAlert("Alert", message: JSON["message"] as! String)
                        }
                    }
                }else{
                    self.view.endEditing(true)
                    self.simpleAlert("Error Sending Message", message: "Please check your internet connection...")
                }
        }//end api
    }
    func getAllMessagesApi(){
        /*let chatData = [
            "userid" : userID,
            "eventid" : eventID,
            "messageall" : ""
        ]*/
        
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "eventid" : eventID,
            "messageall" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    if let JSON = response.result.value {
                       // print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let msg = JSON["message"] as! NSArray
                           // print(msg)
                           /* for i in 0 ..< msg.count {
                                if let  mm = msg[i]["message"] as? String {
                                    self.arrChatMessage.append(mm)
                                }
                                if let fname = msg[i]["fname"] as? String {
                                    if let lname = msg[i]["lname"] as? String{
                                        self.arrChatUserName.append("\(fname) \(lname)")
                                    }
                                }
                                if let id = msg[i]["user_id"] as? String {
                                        self.arrChatWriterID.append(id)
                                }
                            }//end for loop*/
                            print("getAllMessages")
                            if self.count != msg.count{
                                for i in self.count ..< msg.count {
                                    if let  mm = msg[i]["message"] as? String {
                                        //arrChatMessage.append(mm)
                                        if let fname = msg[i]["fname"] as? String {
                                            if let lname = msg[i]["lname"] as? String{
                                                //arrChatUserName.append("\(fname) \(lname)")
                                                if let id = msg[i]["user_id"] as? String {
                                                    // arrChatWriterID.append(id)
                                                    if !(self.onceOnly){
                                                        self.messages.append(JSQMessage(senderId: id, displayName: "\(fname) \(lname)", text: mm))
                                                        self.finishReceivingMessageAnimated(false)
                                                    }else{
                                                            if id != userID{
                                                                self.messages.append(JSQMessage(senderId: id, displayName: "\(fname) \(lname)", text: mm))
                                                                self.finishReceivingMessageAnimated(true)
                                                            }
                                                        
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }//end for loop
                                if let  chatIdd = msg[msg.count-1]["chatid"] as? String {
                                   self.chatID = chatIdd
                                        self.readStatusApi(chatIdd)

                                }
                                
                            }
                            
                            self.count = msg.count
                            self.collectionView.reloadData()
                           
                            self.onceOnly = true
                        }else{
                            self.onceOnly = true
                            
                        }
                    }
                }else{
                    print("NETWORK ERROR")
                }
        }//end api
    }
    func readStatusApi(chatid: String){
        print(chatid)
            Alamofire.request(.POST, apiLink, parameters: [
                "userid" : userID,
                "eventid" : self.eventID,
                "chatid" : chatid,
                    "read" : ""
                ])
                .responseJSON {response in
                    if String(response.result) == "SUCCESS"{
                        if let JSON = response.result.value {
                                // print("JSON: \(JSON)")
                            apiStatus = JSON["status"] as! String
                            if apiStatus == "success" {
                                let message = JSON["message"] as! String
                                print(message)
                            
                            }
                        }
                    }
                }//end api
        
    }
    //MARK: JSQMessages CollectionView DataSource
    
    /* override func senderId() -> String {
        return User.Wazniak.rawValue
    }
     func senderDisplayName() -> String {
        return getName(User.Wazniak)
    }*/
    override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return messages.count
    }
    override func collectionView(collectionView: JSQMessagesCollectionView, messageDataForItemAtIndexPath indexPath: NSIndexPath) -> JSQMessageData {
       // let message = self.messages[indexPath.item]
       
        return messages[indexPath.item]
    }
    override func collectionView(collectionView: JSQMessagesCollectionView, messageBubbleImageDataForItemAtIndexPath indexPath: NSIndexPath) -> JSQMessageBubbleImageDataSource {
        
        return messages[indexPath.item].senderId == userID ? outgoingBubble : incomingBubble
    }
    override func collectionView(collectionView: JSQMessagesCollectionView, attributedTextForMessageBubbleTopLabelAtIndexPath indexPath: NSIndexPath) -> NSAttributedString? {
        let message = messages[indexPath.item]
        //Here we are displaying everyones name above their message except for the "Senders"
        if message.senderId == userID || message.text == ""{
            return nil
        }
       
        return NSAttributedString(string: message.senderDisplayName)
    }
    override func collectionView(collectionView: JSQMessagesCollectionView, layout collectionViewLayout: JSQMessagesCollectionViewFlowLayout, heightForMessageBubbleTopLabelAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        let message = messages[indexPath.item]
        if message.text == ""{
            return 0.0
        }

        return messages[indexPath.item].senderId == userID ? 0 : kJSQMessagesCollectionViewCellLabelHeightDefault
    }
    override func collectionView(collectionView: JSQMessagesCollectionView, attributedTextForCellTopLabelAtIndexPath indexPath: NSIndexPath) -> NSAttributedString? {
        /**
         *  This logic should be consistent with what you return from `heightForCellTopLabelAtIndexPath:`
         *  The other label text delegate methods should follow a similar pattern.
         *
         *  Show a timestamp for every 3rd message
         */
            let message = self.messages[indexPath.item]
            if message.text == ""{
                if message.senderId != userID{
                    return NSMutableAttributedString(string: "\(message.senderDisplayName) is added")
                }else{
                    return NSMutableAttributedString(string: "You were added")
                }
            }
            //return JSQMessagesTimestampFormatter.sharedFormatter().attributedTimestampForDate(message.date)
       
        return nil
    }
    override func collectionView(collectionView: JSQMessagesCollectionView, layout collectionViewLayout: JSQMessagesCollectionViewFlowLayout, heightForCellTopLabelAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        /**
         *  This logic should be consistent with what you return from `attributedTextForCellTopLabelAtIndexPath:`
         *  The other label height delegate methods should follow similarly
         *
         *  Show a timestamp for every 3rd message
         */
        let message = self.messages[indexPath.item]
        if message.text == "" {
            return kJSQMessagesCollectionViewCellLabelHeightDefault
        }
        return 0.0
    }
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated)
        timer.invalidate()
        if timer != "" {
            timer = NSTimer()
        }
        shouldShow = true;
    }
    func actionImage(){
        self.view.endEditing(true)
        actionPList(eventID)
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
