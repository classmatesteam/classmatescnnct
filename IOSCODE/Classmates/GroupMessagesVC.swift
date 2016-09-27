//
//  GroupMessagesVC.swift
//  Classmates
//
//  Created by Mallu on 6/22/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
class GroupMessagesVC: UIViewController, UITableViewDelegate, UITextFieldDelegate {
    @IBOutlet weak var tblMessages: UITableView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewSearch: UIView!
    
    //var userID = ""
    var arrEventId = [String]()
    var arrEvent: [String] = [String]()
    //var arrUserName: [String] = [String]()
    var arrTopicName: [String] = [String]()
    var arrCreatedTime: [String] = [String]()
    var arrPic = [String]()
    var arrReadStatus = [Int]()
    var arrLastMessage: [String] = [String]()
    
    //filter array(7)
    var arrFilterEventID = [String]()
    var arrFilterEventName = [String]()
    var arrFilterTopicName = [String]()
    var arrFilterCreatedTime = [String]()
    var arrFilterPic = [String]()
    var arrFilterReadStatus = [Int]()
    var arrFilterLastMessage: [String] = [String]()
    
    var textField = UITextField()
    let cancelButton = UIButton()
    var filterCheck = false
    
    var someProperty = ""
    var timeProperty = ""
    var timerGpList: NSTimer = NSTimer()
    var boolOnce = true
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
        shouldShow = false;
        gpMessages()
        self.showHUD()
        timerGpList = NSTimer.scheduledTimerWithTimeInterval(5.0, target: self, selector: #selector(GroupMessagesVC.gpMessages), userInfo: nil, repeats: true)
    }
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(true)
        timerGpList.invalidate()
        if timerGpList != "" {
            timerGpList = NSTimer()
        }
        shouldShow = true;
    }
    func configureDesign(){
        userID = defaults.stringForKey("userID")!
        tblMessages.rowHeight = 70
       // initAppearNavigation(viewTop)
        tblMessages.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblMessages.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(GroupMessagesVC.actionBack)))
        self.viewSearch.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(GroupMessagesVC.actionSearch)))
        
        textField = UITextField(frame: CGRectMake(tblMessages.frame.origin.x + 20, tblMessages.frame.origin.y + 10, tblMessages.frame.size.width - 90, 32.0))
        textField.attributedPlaceholder = NSAttributedString(string: "Search Course Name", attributes: [NSForegroundColorAttributeName: colorLightBlue])
        filterTextField(textField)
        //textField.autocapitalizationType = UITextAutocapitalizationType.AllCharacters
        
        filterButton(cancelButton)
        cancelButton.frame = CGRectMake(textField.frame.origin.x + textField.frame.size.width + 10, textField.frame.origin.y, 60, 32)
        cancelButton.addTarget(self, action: #selector(GroupMessagesVC.pressed(_:)), forControlEvents: .TouchUpInside)
        textField.delegate = self
    }
    func gpMessages(){
     
        /*let data = [
            "userid" : userID,
            "grouplist" : ""
        ]*/
        
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "grouplist" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        //print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            self.arrEvent.removeAll()
                            //arrUserName.removeAll()
                            self.arrEventId.removeAll()
                            self.arrPic.removeAll()
                            self.arrTopicName.removeAll()
                            self.arrCreatedTime.removeAll()
                            self.arrReadStatus.removeAll()
                            self.arrLastMessage.removeAll()
                            let message = JSON["message"] as! NSArray
                            //print(message)
                            for i in 0 ..< message.count {
                                if let  displayGroup = message[i]["display"] as? String {
                                   if displayGroup == "Yes"{
                                        if let  Course_Name = message[i]["name"] as? String {
                                            self.arrEvent.append(Course_Name)
                                        }
                                        /* if let fname = message[i]["fname"] as? String {
                                        if let lname = message[i]["lname"] as? String{
                                        self.arrUserName.append("\(fname) \(lname)")
                                        }
                                        }*/
                                        if let userpic = message[i]["userpic"] as? String {
                                            self.arrPic.append(userpic)
                                        }
                                        if let eventid = message[i]["eventid"] as? String {
                                            self.arrEventId.append(eventid)
                                        }
                                        if let topicName = message[i]["topicname"] as? String {
                                            self.arrTopicName.append(topicName)
                                        }
                                        if let createdtime = message[i]["lastmsgtime"] as? String {
                                            if let readStatus = message[i]["read"] as? Int {
                                                self.arrReadStatus.append(readStatus)
                                                if createdtime == "NULL"{
                                                    self.arrCreatedTime.append("You were added.")
                                                    self.arrLastMessage.append("You were added.")// set time
                                                }else{
                                                    self.arrCreatedTime.append(createdtime)//set time
                                                    if let lastMsg = message[i]["lastmsg"] as? String {
                                                        if lastMsg != "" {
                                                            
                                                            self.arrLastMessage.append(lastMsg)
                                                        }else{
                                                            self.arrLastMessage.append("")
                                                        }
                                                        /*if readStatus == 0 && lastMsg != "" {
                                                            self.arrLastMessage.append(lastMsg)
                                                        }else{
                                                            self.arrLastMessage.append(createdtime)
                                                        }*/
                                                    }
                                                    
                                                }
                                            }
                                        }
                                }//end display yes condn
                                }
                            
                            }//end for loop
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            //self.simpleAlert("Alert", message: "Sorry, Group List Not Found.")
                        }
                    }
                    self.tblMessages.reloadData()
                }else{
                    self.hideHUD()
                    if self.boolOnce == true {
                        self.boolOnce = false
                        self.simpleAlert("Network Error", message: "Please check your internet connection...")
                    }
                    
                }
        }//end api
    }
    func actionSearch(){
        self.view.addSubview(textField)
        self.view.addSubview(cancelButton)
        tblMessages.frame.origin.y = (self.textField.frame.origin.y) + (self.textField.frame.size.height) + 2
        tblMessages.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.textField.frame.origin.y) + (self.textField.frame.size.height))
    }
    func pressed(sender: UIButton!) {
        textField.text = ""
        filterCheck = false
        cancelButton.removeFromSuperview()
        textField.removeFromSuperview()
        tblMessages.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblMessages.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        tblMessages.reloadData()
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    func textField(textField: UITextField, shouldChangeCharactersInRange range: NSRange, replacementString string: String) -> Bool {
        let str = (textField.text! as NSString).stringByReplacingCharactersInRange(range, withString: string.uppercaseString)
        let currentCharacterCount = str.characters.count ?? 0
        if currentCharacterCount >= 7{
            //let myNSString = str as NSString
            //textField.text = myNSString.substringWithRange(NSRange(location: 0, length: 5))
            //filter(myNSString.substringWithRange(NSRange(location: 0, length: 5)))
        }else{
            textField.text = str
            filter(str)
        }
        return false
    }
    func filter(text: String){
        
        arrFilterEventName.removeAll()
        arrFilterTopicName.removeAll()
        arrFilterEventID.removeAll()
        arrFilterCreatedTime.removeAll()
        arrFilterPic.removeAll()
        arrFilterReadStatus.removeAll()
        arrFilterLastMessage.removeAll()
        for i in 0 ..< arrEvent.count {
            if (arrEvent[i].lowercaseString).containsString(text.lowercaseString){
                
                arrFilterEventName.append(arrEvent[i])
                arrFilterTopicName.append(arrTopicName[i])
                arrFilterCreatedTime.append(arrCreatedTime[i])
                arrFilterLastMessage.append(arrLastMessage[i])
                arrFilterPic.append(arrPic[i])
                arrFilterEventID.append(arrEventId[i])
                arrFilterReadStatus.append(arrReadStatus[i])
            }
        }
        filterCheck = true
        tblMessages.reloadData()
    }
    //MARK: TableView Delegate Methods
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        if filterCheck == false{
            return self.arrEventId.count
        }else{
            return self.arrFilterEventID.count
        }
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> MessagesTableViewCell{
        
        let cell = tblMessages.dequeueReusableCellWithIdentifier("gpMsgId") as! MessagesTableViewCell
        if indexPath.row == 1 {
            cell.viewSide.backgroundColor = UIColor.clearColor()
            cell.itemName.font = UIFont(name: "Ubuntu", size: 15.0)
        }
        if filterCheck == false{
            cell.itemName.text = "\(arrEvent[indexPath.row]) (\(arrTopicName[indexPath.row]))"
            cell.itemUser.text = arrLastMessage[indexPath.row]
            imgUrlString = arrPic[indexPath.row]
            if arrReadStatus[indexPath.row] == 0 && arrCreatedTime[indexPath.row] != "You were added." {
                cell.viewSide.backgroundColor = UIColor.clearColor()
                cell.itemName.font = UIFont(name: "Ubuntu-Bold", size: 15.0)
                cell.itemUser.font = UIFont(name: "Ubuntu-Bold", size: 14.0)
                cell.itemUser.textColor = UIColor.whiteColor()
                cell.itemName.textColor = UIColor.whiteColor()
            }else{
                cell.viewSide.backgroundColor = UIColor.clearColor()
                //cell.itemName.font = UIFont(name: "Ubuntu", size: 15.0)
                cell.itemName.font = UIFont(name: "Ubuntu-Regular", size: 14.0)
                cell.itemUser.font = UIFont(name: "Ubuntu", size: 13.0)
                cell.itemUser.textColor = UIColor.lightGrayColor()
                cell.itemName.textColor = UIColor.lightGrayColor()
            }
            if imgUrlString.containsString("http"){
                ImageLoader.sharedLoader.imageForUrl(imgUrlString, completionHandler:{(image: UIImage?, url: String) in
                    cell.itemImg.image = image
                })
                // imageURL = NSURL(string: imgUrlString)!
            }else{
                ImageLoader.sharedLoader.imageForUrl("\(imgApiLink)\(imgUrlString)", completionHandler:{(image: UIImage?, url: String) in
                    cell.itemImg.image = image
                })
                //imageURL = NSURL(string: "http://brightdeveloper.net/classmates/assets/user/\(imgUrlString)")!
            }
        }else{
            cell.itemName.text = "\(arrFilterEventName[indexPath.row]) (\(arrFilterTopicName[indexPath.row]))"
            cell.itemUser.text = arrFilterLastMessage[indexPath.row]
            if arrFilterReadStatus[indexPath.row] == 0 && arrFilterCreatedTime[indexPath.row] != "You were added"{
                cell.backgroundColor = colorLightBlue
            }else{
                 cell.backgroundColor = UIColor.clearColor()
            }
            imgUrlString = arrPic[indexPath.row]
            
            if imgUrlString.containsString("http"){
                ImageLoader.sharedLoader.imageForUrl(imgUrlString, completionHandler:{(image: UIImage?, url: String) in
                    cell.itemImg.image = image
                })
                // imageURL = NSURL(string: imgUrlString)!
            }else{
                ImageLoader.sharedLoader.imageForUrl("\(imgApiLink)\(imgUrlString)", completionHandler:{(image: UIImage?, url: String) in
                    cell.itemImg.image = image
                })
                //imageURL = NSURL(string: "http://brightdeveloper.net/classmates/assets/user/\(imgUrlString)")!
            }
        }
       
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                if error == nil {
                    cell.itemImg.image = UIImage(data: data!)
                }
        })*/
        return cell
    }
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        if chatNext == false {
            chatNext = true
            if filterCheck == false{
                self.someProperty = "\(self.arrEvent[indexPath.row])(\(self.arrTopicName[indexPath.row]))"
                self.timeProperty = self.arrCreatedTime[indexPath.row]
                defaults.setObject(arrEventId[indexPath.row], forKey: "eventID")
            }else{
                self.someProperty = "\(self.arrFilterEventName[indexPath.row])(\(self.arrFilterTopicName[indexPath.row]))"
                self.timeProperty = self.arrFilterCreatedTime[indexPath.row]
                defaults.setObject(arrFilterEventID[indexPath.row], forKey: "eventID")
            }
            //callGetAllMessagesApi()
            self.performSegueWithIdentifier("segueChat", sender: tableView)
        }
        
    }
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    /*func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath){
        
        switch editingStyle{
        case .Delete:
            print(indexPath.row)
        default:
            return
        }
    }*/
    func tableView(tableView: UITableView, editActionsForRowAtIndexPath indexPath: NSIndexPath) -> [UITableViewRowAction]? {
        let delete = UITableViewRowAction(style: .Destructive, title: "Delete\nChat") { (action, indexPath) in
            // delete item at indexPath
            print(indexPath.row)
            self.actionDeleteChat(indexPath)
        }
        delete.backgroundColor = UIColor.redColor()
        //delete.backgroundColor = UIColor(patternImage: UIImage(named: "deleteIcon")!)
        return [delete]
    }
    func actionDeleteChat(indexPath: NSIndexPath){
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        var id = ""
        var msg = ""
        if self.filterCheck == false{
            id = self.arrEventId[indexPath.row]
            msg = self.arrCreatedTime[indexPath.row]
            //print(self.arrEventId[indexPath.row])
        }else{
            id = self.arrFilterEventID[indexPath.row]
            msg = self.arrFilterCreatedTime[indexPath.row]
        }
        print("User: \(userID)")
        if msg != "You were added." {
        let alert = UIAlertController(title: "Alert", message: "Are you sure you want to delete the chat?", preferredStyle: .Alert)
        alert.addAction(UIAlertAction(title: "Yes", style: .Default){
            UIAlertAction in
            //remove
            /*let data = [
                "eventid" : id,
                "userid" : self.userID,
                "deletemessage" : ""
            ]*/
            self.showHUD()
            Alamofire.request(.POST, apiLink, parameters: [
                "eventid" : id,
                "userid" : userID,
                "deletemessage" : ""
                ])
                .responseJSON {response in
                    //print(response.result)
                    if String(response.result) == "SUCCESS"{
                        
                        if let JSON = response.result.value {
                            print("JSON: \(JSON)")
                            apiStatus = JSON["status"] as! String
                            if apiStatus == "success" {
                                if self.filterCheck == false{
                                    /*for i in 0 ..< arrDefaultEventID.count {
                                        if arrDefaultEventID[i] == id{
                                            arrDefaultEventID.removeAtIndex(i)
                                            arrDefaultCourseName.removeAtIndex(i)
                                            arrDefaultTopicName.removeAtIndex(i)
                                            arrDefaultStartTime.removeAtIndex(i)
                                            arrDefaultLocation.removeAtIndex(i)
                                            arrDefaultCreatedDate.removeAtIndex(i)
                                            arrDefaultParticipants.removeAtIndex(i)
                                            break
                                        }
                                    }*/
                                    self.arrEventId.removeAtIndex(indexPath.row)
                                    self.arrEvent.removeAtIndex(indexPath.row)
                                    self.arrTopicName.removeAtIndex(indexPath.row)
                                    self.arrCreatedTime.removeAtIndex(indexPath.row)
                                    self.arrLastMessage.removeAtIndex(indexPath.row)
                                    self.arrPic.removeAtIndex(indexPath.row)
                                    self.arrReadStatus.removeAtIndex(indexPath.row)
                                    //self.arrParticipants.removeAtIndex(indexPath.row)
                                    
                                    
                                    self.tblMessages.reloadData()
                                    self.hideHUD()
                                    self.simpleAlert("Alert", message: "Deleted Successfully.")
                                }else{
                                    print("filterDelete")
                                    self.arrFilterEventID.removeAtIndex(indexPath.row)
                                    self.arrFilterEventName.removeAtIndex(indexPath.row)
                                    self.arrFilterTopicName.removeAtIndex(indexPath.row)
                                    self.arrFilterCreatedTime.removeAtIndex(indexPath.row)
                                    self.arrFilterLastMessage.removeAtIndex(indexPath.row)
                                    self.arrFilterPic.removeAtIndex(indexPath.row)
                                    self.arrFilterReadStatus.removeAtIndex(indexPath.row)
                                    //self.arrFilterParticipants.removeAtIndex(indexPath.section)
                                    for i in 0 ..< self.arrEventId.count {
                                        if self.arrEventId[i] == id{
                                            self.arrEventId.removeAtIndex(i)
                                            self.arrEvent.removeAtIndex(i)
                                            self.arrTopicName.removeAtIndex(i)
                                            self.arrCreatedTime.removeAtIndex(i)
                                            self.arrLastMessage.removeAtIndex(i)
                                            self.arrPic.removeAtIndex(i)
                                            self.arrReadStatus.removeAtIndex(i)
                                            //self.arrParticipants.removeAtIndex(i)
                                            break
                                        }
                                    }
                                    /*for i in 0 ..< arrDefaultEventID.count {
                                        if arrDefaultEventID[i] == id{
                                            arrDefaultEventID.removeAtIndex(i)
                                            arrDefaultCourseName.removeAtIndex(i)
                                            arrDefaultTopicName.removeAtIndex(i)
                                            arrDefaultStartTime.removeAtIndex(i)
                                            arrDefaultLocation.removeAtIndex(i)
                                            arrDefaultCreatedDate.removeAtIndex(i)
                                            arrDefaultParticipants.removeAtIndex(i)
                                            break
                                        }
                                    }*/
                                    self.tblMessages.reloadData()
                                    self.hideHUD()
                                    self.simpleAlert("Alert", message: "Deleted Successfully.")
                                }
                            }else{
                                self.hideHUD()
                                self.simpleAlert("Alert", message: JSON["message"] as! String)
                            }
                        }
                    }else{
                        self.hideHUD()
                        self.simpleAlert("Network Error", message: "Please check your internet connection...")
                    }
            }//end api
            })
        alert.addAction(UIAlertAction(title: "No", style: .Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
            
        }else{
            self.simpleAlert("Alert", message: "Deleted Successfully.")
        }
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        guard let destination = segue.destinationViewController as? ChatViewController else {
                return
        }
       destination.groupName = someProperty
       destination.createdTime = timeProperty
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}