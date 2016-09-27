//
//  HotTopicVC.swift
//  Classmates
//
//  Created by Mallu on 6/22/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
class HotTopicVC: UIViewController, UITableViewDelegate, UITextFieldDelegate {

    @IBOutlet weak var tblHotTopics: UITableView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewFilter: UIView!
    @IBOutlet weak var viewSort: UIView!
    //(10)
    var arrCourseName = [String]()
    var arrTopicName = [String]()
    var arrStartTime = [String]()
    var arrLocation = [String]()
    var arrUserName = [String]()
    var arrPic = [String]()
    var arrParticipants = [Int]()
    var arrEventID = [String]()
    var arrEventCUserID = [String]()
    var arrCreatedDate = [String]()
    
    //var userID = ""

    
    //filter array(10)
    var arrFilterEventID = [String]()
    var arrFilterCourseName = [String]()
    var arrFilterTopicName = [String]()
    var arrFilterStartTime = [String]()
    var arrFilterLocation = [String]()
    var arrFilterUserName = [String]()
    var arrFilterPic = [String]()
    var arrFilterParticipants = [Int]()
    var arrFilterEventCUserID = [String]()
    var arrFilterCreatedDate = [String]()
    //var arrFilterStatus = [String]()
    
    var textField = UITextField()
    let cancelButton = UIButton()
    var filterCheck = false
    var onceOnly = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
        
    }
    func configureDesign(){
        userID = defaults.stringForKey("userID")!
        tblHotTopics.rowHeight = 165
        //initAppearNavigation(viewTop)
        tblHotTopics.separatorStyle = .None
        tblHotTopics.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblHotTopics.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
         self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(GroupMessagesVC.actionBack)))
        print(userID)
        /*let data = [
            "userid" : userID,
            "HotTopics" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "time_zone" : defaults.stringForKey("timeZone")!,
            "HotTopics" : ""
            ])
            .responseJSON {response in
                print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let message = JSON["message"] as! NSArray
                            print(message)
                            for i in 0 ..< message.count {
                                if let user_id = message[i]["user_id"] as? String {
                                    if user_id != userID{
                                        self.arrEventCUserID.append(user_id)
                                        if let  Course_Name = message[i]["Course_Name"] as? String {
                                            self.arrCourseName.append(Course_Name)
                                        }
                                        if let topic = message[i]["topic"] as? String {
                                            self.arrTopicName.append(topic)
                                        }
                                        if let starttime = message[i]["starttime"] as? String {
                                            self.arrStartTime.append(starttime)
                                        }
                                        if let location = message[i]["location"] as? String {
                                            self.arrLocation.append(location)
                                        }
                                        if let fname = message[i]["fname"] as? String {
                                            if let lname = message[i]["lname"] as? String{
                                                self.arrUserName.append("\(fname) \(lname)")
                                            }
                                        }
                                        if let participants = message[i]["participants"] as? Int {
                                            self.arrParticipants.append(participants)
                                        }
                                        if let userpic = message[i]["userpic"] as? String {
                                            self.arrPic.append(userpic)
                                        }
                                        if let event_id = message[i]["event_id"] as? String {
                                            self.arrEventID.append(event_id)
                                        }
                                        if let created = message[i]["eventdate"] as? String {
                                            self.arrCreatedDate.append(created)
                                        }
                                    }//end userID cond
                                    
                                }
                                
                               
                            }//end for loop
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            //self.simpleAlert("Alert", message: "Sorry, No Recommended Study Found.")
                        }
                    }
                   self.tblHotTopics.reloadData()
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api

        //filter table
        self.viewFilter.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(HotTopicVC.actionFilter)))
         self.viewSort.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(HotTopicVC.actionSort)))
        
        textField = UITextField(frame: CGRectMake(tblHotTopics.frame.origin.x, tblHotTopics.frame.origin.y + 10, tblHotTopics.frame.size.width - 70, 32.0))
        textField.attributedPlaceholder = NSAttributedString(string: "Search Course Name", attributes: [NSForegroundColorAttributeName: colorLightBlue])
        filterTextField(textField)
        textField.autocapitalizationType = UITextAutocapitalizationType.AllCharacters
        
        filterButton(cancelButton)
        cancelButton.frame = CGRectMake(textField.frame.origin.x + textField.frame.size.width + 10, textField.frame.origin.y, 60, 32)
        cancelButton.addTarget(self, action: #selector(HotTopicVC.pressed(_:)), forControlEvents: .TouchUpInside)
        
        textField.delegate = self
    }
    func actionFilter(){
        
        self.view.addSubview(textField)
        self.view.addSubview(cancelButton)
        tblHotTopics.frame.origin.y = (self.textField.frame.origin.y) + (self.textField.frame.size.height) + 2
        tblHotTopics.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.textField.frame.origin.y) + (self.textField.frame.size.height))
    }
    func pressed(sender: UIButton!) {
        textField.text = ""
        filterCheck = false
        cancelButton.removeFromSuperview()
        textField.removeFromSuperview()
        tblHotTopics.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblHotTopics.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        tblHotTopics.reloadData()
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
            //myNSString.substringWithRange(NSRange(location: 0, length: 5))
            //filter(myNSString.substringWithRange(NSRange(location: 0, length: 5)))
        }else{
            textField.text = str
            filter(str)
        }
        return false
    }
    func filter(text: String){
        arrFilterEventID.removeAll()
        arrFilterCourseName.removeAll()
        arrFilterTopicName.removeAll()
        arrFilterStartTime.removeAll()
        arrFilterLocation.removeAll()
        arrFilterUserName.removeAll()
        arrFilterPic.removeAll()
        arrFilterParticipants.removeAll()
        arrFilterEventCUserID.removeAll()
        arrFilterCreatedDate.removeAll()
        //arrFilterStatus.removeAll()
        
        for i in 0 ..< arrEventID.count {
            if arrCourseName[i].containsString(text){
                arrFilterEventID.append(arrEventID[i])
                arrFilterCourseName.append(arrCourseName[i])
                arrFilterTopicName.append(arrTopicName[i])
                arrFilterStartTime.append(arrStartTime[i])
                arrFilterLocation.append(arrLocation[i])
                arrFilterUserName.append(arrUserName[i])
                arrFilterPic.append(arrPic[i])
                arrFilterParticipants.append(arrParticipants[i])
                arrFilterEventCUserID.append(arrEventCUserID[i])
                arrFilterCreatedDate.append(arrCreatedDate[i])
                //arrFilterStatus.append(arrStatus[i])
            }
        }
        filterCheck = true
        tblHotTopics.reloadData()
    }

    //MARK: TableView Delegate Methods
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        
        if filterCheck == false{
            return self.arrEventID.count
        }else{
            return self.arrFilterEventID.count
        }
    }
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return 1
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> HotTopicsTableViewCell{
        let cell = tblHotTopics.dequeueReusableCellWithIdentifier("hotTopicId") as! HotTopicsTableViewCell
        cell.itemDate.text = ""
        if filterCheck == false{
            cell.itemCourse.text = arrCourseName[indexPath.section]
            cell.itemTopic.text = arrTopicName[indexPath.section]
            //cell.itemTime.text = arrStartTime[indexPath.section]
            let words: NSArray = arrStartTime[indexPath.section].componentsSeparatedByCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
            
            var date = ""
            for i in 0 ..< words.count {
                if i == 0{
                    date = "\(date)\(words[i])\n"
                }else{
                    date = "\(date) \(words[i])"
                }
            }
            
            cell.itemTime.text = date
            cell.itemVenue.text = arrLocation[indexPath.section]
            cell.itemParticipants.setTitle(String(arrParticipants[indexPath.section]), forState: .Normal)
            cell.itemUserName.text = arrUserName[indexPath.section]
            //cell.itemDate.text = "Date Created: \(arrCreatedDate[indexPath.section])"
            imgUrlString = arrPic[indexPath.section]
       
        }else{
            cell.itemCourse.text = arrFilterCourseName[indexPath.section]
            cell.itemTopic.text = arrFilterTopicName[indexPath.section]
            //cell.itemTime.text = arrFilterStartTime[indexPath.section]
            let words: NSArray = arrFilterStartTime[indexPath.section].componentsSeparatedByCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
            
            var date = ""
            for i in 0 ..< words.count {
                if i == 0{
                    date = "\(date)\(words[i])\n"
                }else{
                    date = "\(date) \(words[i])"
                }
            }
            
            cell.itemTime.text = date
            cell.itemVenue.text = arrFilterLocation[indexPath.section]
            cell.itemParticipants.setTitle(String(arrFilterParticipants[indexPath.section]), forState: .Normal)
            cell.itemUserName.text = arrFilterUserName[indexPath.section]
            imgUrlString = arrFilterPic[indexPath.section]
           // cell.itemDate.text = "Date Created: \(arrFilterCreatedDate[indexPath.section])"
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
        
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                if error == nil {
                    cell.itemImg.image = UIImage(data: data!)
                }
        })*/

        cell.layer.cornerRadius = 10
        cell.layer.borderWidth = 2.0
        cell.layer.borderColor = UIColor.lightGrayColor().CGColor
       
        cell.btnJoin.addTarget(self, action: #selector(HotTopicVC.actionJoin(_:)), forControlEvents: .TouchUpInside)
        cell.itemParticipants.addTarget(self, action: #selector(HotTopicVC.actionParticipants), forControlEvents: .TouchUpInside)
        return cell
    }
    func actionParticipants(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        if filterCheck == false{
            actionPList(arrEventID[indexPath.section])
        }else{
            actionPList(arrFilterEventID[indexPath.section])
        }
    }
    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView?
    {
        let headerView = UIView(frame: CGRectMake(0, 0, tableView.bounds.size.width, 15))
        headerView.backgroundColor = UIColor.clearColor()
        return headerView
    }
   
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    func getButtonIndexPath(button: UIButton) -> NSIndexPath {
        let buttonFrame: CGRect = button.convertRect(button.bounds, toView: tblHotTopics)
        return self.tblHotTopics.indexPathForRowAtPoint(buttonFrame.origin)!
    }
    func actionJoin(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        var eventId = ""
        var createdUserId = ""
        if filterCheck == false{
            eventId = arrEventID[indexPath.section]
            createdUserId = arrEventCUserID[indexPath.section]
        }else{
            eventId = arrFilterEventID[indexPath.section]
            createdUserId = arrFilterEventCUserID[indexPath.section]
        }
        /*print(eventId)
        print(createdUserId)
        let data = [
            "userid" : createdUserId,
            "eventid" : eventId,
            "friendsid": userID,
            "join" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : createdUserId,
            "eventid" : eventId,
            "friendsid": userID,
            "join" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            
                            if self.filterCheck == false{
                                for i in 0 ..< arrDefaultEventID.count {
                                    if arrDefaultEventID[i] == eventId{
                                        //(10)
                                        arrDefaultEventID.removeAtIndex(i)
                                        arrDefaultCourseName.removeAtIndex(i)
                                        arrDefaultTopicName.removeAtIndex(i)
                                        arrDefaultStartTime.removeAtIndex(i)
                                        arrDefaultLocation.removeAtIndex(i)
                                        arrDefaultCreatedDate.removeAtIndex(i)
                                        arrDefaultParticipants.removeAtIndex(i)
                                        
                                        arrDefaultEventCUserID.removeAtIndex(i)
                                        arrDefaultEventCUserName.removeAtIndex(i)
                                        arrDefaultCUserPic.removeAtIndex(i)
                                        break
                                    }
                                }
                                self.arrEventID.removeAtIndex(indexPath.section)
                                self.arrCourseName.removeAtIndex(indexPath.section)
                                self.arrTopicName.removeAtIndex(indexPath.section)
                                self.arrStartTime.removeAtIndex(indexPath.section)
                                self.arrLocation.removeAtIndex(indexPath.section)
                                self.arrParticipants.removeAtIndex(indexPath.section)
                                self.arrEventCUserID.removeAtIndex(indexPath.section)
                                self.arrUserName.removeAtIndex(indexPath.section)
                                self.arrCreatedDate.removeAtIndex(indexPath.section)
                                self.arrPic.removeAtIndex(indexPath.section)
                                
                                self.tblHotTopics.reloadData()
                                self.hideHUD()
                                self.alertToMsg("You Have Joined This Study Successfully.")
                                //self.simpleAlert("Alert", message: "You have joined this study successfully.")
                            }else{
                                print("filterDelete")
                                self.arrFilterEventID.removeAtIndex(indexPath.section)
                                self.arrFilterCourseName.removeAtIndex(indexPath.section)
                                self.arrFilterTopicName.removeAtIndex(indexPath.section)
                                self.arrFilterStartTime.removeAtIndex(indexPath.section)
                                self.arrFilterLocation.removeAtIndex(indexPath.section)
                                self.arrFilterCreatedDate.removeAtIndex(indexPath.section)
                                self.arrFilterParticipants.removeAtIndex(indexPath.section)
                                
                                self.arrFilterUserName.removeAtIndex(indexPath.section)
                                self.arrFilterEventCUserID.removeAtIndex(indexPath.section)
                                self.arrFilterPic.removeAtIndex(indexPath.section)
                                
                                for i in 0 ..< self.arrEventID.count {
                                    if self.arrEventID[i] == eventId{
                                        self.arrEventID.removeAtIndex(i)
                                        self.arrCourseName.removeAtIndex(i)
                                        self.arrTopicName.removeAtIndex(i)
                                        self.arrStartTime.removeAtIndex(i)
                                        self.arrLocation.removeAtIndex(i)
                                        self.arrCreatedDate.removeAtIndex(i)
                                        self.arrParticipants.removeAtIndex(i)
                                        
                                        self.arrEventCUserID.removeAtIndex(i)
                                        self.arrUserName.removeAtIndex(i)
                                        self.arrPic.removeAtIndex(i)
                                        break
                                    }
                                }
                                for i in 0 ..< arrDefaultEventID.count {
                                    if arrDefaultEventID[i] == eventId{
                                        arrDefaultEventID.removeAtIndex(i)
                                        arrDefaultCourseName.removeAtIndex(i)
                                        arrDefaultTopicName.removeAtIndex(i)
                                        arrDefaultStartTime.removeAtIndex(i)
                                        arrDefaultLocation.removeAtIndex(i)
                                        arrDefaultCreatedDate.removeAtIndex(i)
                                        arrDefaultParticipants.removeAtIndex(i)
                                        
                                        arrDefaultEventCUserID.removeAtIndex(i)
                                        arrDefaultEventCUserName.removeAtIndex(i)
                                        arrDefaultCUserPic.removeAtIndex(i)
                                        break
                                    }
                                }
                                self.tblHotTopics.reloadData()
                                self.hideHUD()
                                self.alertToMsg("You Have Joined This Study Successfully.")
                                //self.simpleAlert("Alert", message: "You have joined this study successfully.")
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
    }
    func actionSort()  {
        if !onceOnly {
            arrDefaultEventID = arrEventID
            arrDefaultCourseName = arrCourseName
            arrDefaultTopicName = arrTopicName
            arrDefaultStartTime = arrStartTime
            arrDefaultLocation = arrLocation
            arrDefaultCreatedDate = arrCreatedDate
            arrDefaultParticipants = arrParticipants
            arrDefaultEventCUserName = arrUserName
            arrDefaultCUserPic = arrPic
            arrDefaultEventCUserID = arrEventCUserID
           // arrDefaultStatus = arrStatus
            onceOnly = true
        }
        arrSortEventID.removeAll()
        arrSortCourseName.removeAll()
        arrSortTopicName.removeAll()
        arrSortStartTime.removeAll()
        arrSortLocation.removeAll()
        arrSortCreatedDate.removeAll()
        arrSortUserName.removeAll()
        arrSortPic.removeAll()
        arrSortParticipants.removeAll()
        arrSortEventCUserID.removeAll()
        //arrSortStatus.removeAll()
        
        
        var dictOfIntegers = [Int: String]()
        
        for i in 0 ..< arrCourseName.count {
            dictOfIntegers[i] = arrCourseName[i]
        }
        print(dictOfIntegers)
        
        
        /*for (k,v) in Array(dictOfIntegers).sort(<) {
         print("\(k):\(v)")
         }
         let sortedKeys = (dictOfIntegers.values).sort(<)
         print(sortedKeys)*/
        let keyValueArray = dictOfIntegers.sort{ $0.0 < $1.0 }.sort{ $0.1 < $1.1}
        
        for (key, value) in keyValueArray {
            print(key, value)
        }
        let alertController = UIAlertController(title: "Choose Option", message: "", preferredStyle: UIAlertControllerStyle.Alert)
        let oneAction = UIAlertAction(title: "Course Name", style: .Default) { (action) in
            
            self.pressed(self.cancelButton)
            self.showHUD()
            for (key, value) in keyValueArray {
                print(key, value)
                arrSortEventID.append(self.arrEventID[key])
                arrSortCourseName.append(self.arrCourseName[key])
                arrSortTopicName.append(self.arrTopicName[key])
                arrSortStartTime.append(self.arrStartTime[key])
                arrSortLocation.append(self.arrLocation[key])
                arrSortCreatedDate.append(self.arrCreatedDate[key])
                arrSortUserName.append(self.arrUserName[key])
                arrSortPic.append(self.arrPic[key])
                arrSortParticipants.append(self.arrParticipants[key])
                arrSortEventCUserID.append(self.arrEventCUserID[key])
                //arrSortStatus.append(self.arrStatus[key])
            }
            
            /* self.arrEventID.removeAll()
             self.arrCourseName.removeAll()
             self.arrTopicName.removeAll()
             self.arrStartTime.removeAll()
             self.arrLocation.removeAll()
             self.arrUserName.removeAll()
             self.arrPic.removeAll()
             self.arrParticipants.removeAll()
             self.arrEventCUserID.removeAll()
             self.arrStatus.removeAll()*/
            
            self.arrEventID = arrSortEventID
            self.arrCourseName = arrSortCourseName
            self.arrTopicName = arrSortTopicName
            self.arrStartTime = arrSortStartTime
            self.arrLocation = arrSortLocation
            self.arrCreatedDate = arrSortCreatedDate
            self.arrUserName = arrSortUserName
            self.arrPic = arrSortPic
            self.arrParticipants = arrSortParticipants
            self.arrEventCUserID = arrSortEventCUserID
            //self.arrStatus = arrSortStatus
            
            self.tblHotTopics.reloadData()
            self.hideHUD()
        }
        let twoAction = UIAlertAction(title: "Date & Time", style: .Default) { (action) in
            self.pressed(self.cancelButton)
            
            self.arrEventID = arrDefaultEventID
            self.arrCourseName = arrDefaultCourseName
            self.arrTopicName = arrDefaultTopicName
            self.arrStartTime = arrDefaultStartTime
            self.arrLocation = arrDefaultLocation
            self.arrCreatedDate = arrDefaultCreatedDate
            self.arrUserName = arrDefaultEventCUserName
            self.arrPic = arrDefaultCUserPic
            self.arrParticipants = arrDefaultParticipants
            self.arrEventCUserID = arrDefaultEventCUserID
            //self.arrStatus = arrDefaultStatus
            
            self.tblHotTopics.reloadData()
            
        }
        let cancelAction = UIAlertAction(title: "Cancel", style: .Cancel) { (action) in
        }
        alertController.addAction(oneAction)
        alertController.addAction(twoAction)
        alertController.addAction(cancelAction)
        self.presentViewController(alertController, animated: true, completion: nil)
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}