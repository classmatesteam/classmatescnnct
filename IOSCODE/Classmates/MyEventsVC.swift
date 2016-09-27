//
//  MyEventVC.swift
//  Classmates
//
//  Created by Mallu on 6/22/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import Alamofire
import UIKit

class MyEventsVC: UIViewController, UITableViewDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var tblAllEvents: UITableView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewSort: UIView!
    @IBOutlet weak var viewFilter: UIView!
    @IBOutlet weak var btnCreated: UIButton!
    @IBOutlet weak var btnJoined: UIButton!
    @IBOutlet weak var viewTab: UIView!
    var checkCreated: Bool = false
    var checkJoined: Bool = true
   
    @IBOutlet weak var viewAlert: UIView!
    @IBOutlet weak var btnAddStudy: UIButton!
    @IBOutlet weak var viewAlertC: UIView!
    @IBOutlet weak var btnCreateStudy: UIButton!
    var arrClasses = [String]()
    var arrEventID = [String]()
    var arrCourseName = [String]()
    var arrTopicName = [String]()
    var arrStartTime = [String]()
    var arrLocation = [String]()
    var arrCreatedDate = [String]()
    var arrParticipants = [Int]()
    
    var arrEventCUserName = [String]()
    var arrCUserPic = [String]()
    //var userID = ""
    
    //filter array(7/9)
    var arrFilterEventID = [String]()
    var arrFilterCourseName = [String]()
    var arrFilterTopicName = [String]()
    var arrFilterStartTime = [String]()
    var arrFilterLocation = [String]()
    var arrFilterCreatedDate = [String]()
    var arrFilterParticipants = [Int]()
    
    var arrFilterEventCUserName = [String]()
    var arrFilterCUserPic = [String]()
   
    //var arrFilterEventCUserID = [String]()
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
        
        tblAllEvents.rowHeight = 165
        tblAllEvents.separatorStyle = .None
        //initAppearNavigation(viewTop)
        self.viewTab.frame.origin.y = self.viewTop.frame.origin.y + self.viewTop.frame.size.height
        tblAllEvents.frame.origin.y = (self.viewTab.frame.origin.y) + (self.viewTab.frame.size.height)
        tblAllEvents.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTab.frame.origin.y) + (self.viewTab.frame.size.height))
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(GroupMessagesVC.actionBack)))
       
        btnCreated.layer.cornerRadius = 10.0
        btnCreated.clipsToBounds = true
        btnCreated.layer.borderWidth = 2.0
        btnCreated.layer.borderColor = colorLightBlue.CGColor
        btnCreated.tintColor = colorLightBlue
        btnCreated.backgroundColor = UIColor.whiteColor()
        
        btnJoined.layer.cornerRadius = 10.0
        btnJoined.clipsToBounds = true
        btnJoined.layer.borderWidth = 2.0
        btnJoined.layer.borderColor = UIColor.whiteColor().CGColor
        btnJoined.tintColor = UIColor.whiteColor()
        userID = defaults.stringForKey("userID")!
        myEventApi()
        
        
        //sort and filter
        
        self.viewFilter.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(AllEventsVC.actionFilter)))
         self.viewSort.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(MyEventsVC.actionSort)))
        
        textField = UITextField(frame: CGRectMake(tblAllEvents.frame.origin.x, tblAllEvents.frame.origin.y + 10, tblAllEvents.frame.size.width - 70, 32.0))
        textField.attributedPlaceholder = NSAttributedString(string: "Search Course Name", attributes: [NSForegroundColorAttributeName: colorLightBlue])
        filterTextField(textField)
        textField.autocapitalizationType = UITextAutocapitalizationType.AllCharacters
        
        filterButton(cancelButton)
        cancelButton.frame = CGRectMake(textField.frame.origin.x + textField.frame.size.width + 10, textField.frame.origin.y, 60, 32)
        cancelButton.addTarget(self, action: #selector(MyEventsVC.pressed(_:)), forControlEvents: .TouchUpInside)
        
        textField.delegate = self
        btnAddStudy.addTarget(self, action: #selector(MyEventsVC.actionAlertView), forControlEvents: .TouchUpInside)
        btnCreateStudy.addTarget(self, action: #selector(MyEventsVC.actionAlertViewC), forControlEvents: .TouchUpInside)
        self.viewAlert.hidden = true
        self.viewAlertC.hidden = true
    }
    func actionAlertView(){
    self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("HotTopicVC") as! HotTopicVC, animated: true)
    }
    func actionAlertViewC(){
        let nextVC = self.storyboard?.instantiateViewControllerWithIdentifier("CourseVC") as! CourseVC
        self.navigationController?.pushViewController(nextVC, animated: true)
        nextVC.arrClassList = arrClasses
    }
    func actionFilter(){
        self.view.addSubview(textField)
        self.view.addSubview(cancelButton)
        tblAllEvents.frame.origin.y = (self.textField.frame.origin.y) + (self.textField.frame.size.height) + 2
        tblAllEvents.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.textField.frame.origin.y) + (self.textField.frame.size.height))
    }
    func pressed(sender: UIButton!) {
        textField.text = ""
        filterCheck = false
        cancelButton.removeFromSuperview()
        textField.removeFromSuperview()
        tblAllEvents.frame.origin.y = (self.viewTab.frame.origin.y) + (self.viewTab.frame.size.height)
        tblAllEvents.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTab.frame.origin.y) + (self.viewTab.frame.size.height))
        tblAllEvents.reloadData()
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
           // textField.text = myNSString.substringWithRange(NSRange(location: 0, length: 5))
            //filter(myNSString.substringWithRange(NSRange(location: 0, length: 5)))
        }else{
            textField.text = str
            filter(str)
        }
        return false
    }
    func filter(text: String){
        arrFilterCourseName.removeAll()
        arrFilterTopicName.removeAll()
        arrFilterStartTime.removeAll()
        arrFilterLocation.removeAll()
        
        arrFilterParticipants.removeAll()
       
            arrFilterEventID.removeAll()
            arrFilterCreatedDate.removeAll()
            arrFilterEventCUserName.removeAll()
            arrFilterCUserPic.removeAll()
        
       // arrFilterEventCUserID.removeAll()
       // arrFilterStatus.removeAll()
        
        for i in 0 ..< arrCourseName.count {
            if arrCourseName[i].containsString(text){
                
                arrFilterCourseName.append(arrCourseName[i])
                arrFilterTopicName.append(arrTopicName[i])
                arrFilterStartTime.append(arrStartTime[i])
                arrFilterLocation.append(arrLocation[i])
                arrFilterParticipants.append(arrParticipants[i])
                arrFilterCreatedDate.append(arrCreatedDate[i])
                arrFilterEventID.append(arrEventID[i])
                 if checkJoined == false{
                    arrFilterEventCUserName.append(arrEventCUserName[i])
                    arrFilterCUserPic.append(arrCUserPic[i])
                 }
                //arrFilterEventCUserID.append(arrEventCUserID[i])
                //arrFilterStatus.append(arrStatus[i])
            }
        }
        filterCheck = true
        tblAllEvents.reloadData()
    }
    //MARK: TableView Delegate Methods
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        if filterCheck == false{
            return self.arrCourseName.count
        }else{
            return self.arrFilterCourseName.count
        }
    }
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return 1
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> MyEventsTableViewCell{
        let cell = tblAllEvents.dequeueReusableCellWithIdentifier("myEventsId") as! MyEventsTableViewCell
        cell.itemImg.image = UIImage(named: "prf")
        cell.itemDate.text = ""
        cell.layer.cornerRadius = 10
        cell.layer.borderWidth = 1.5
        cell.layer.borderColor = UIColor.lightGrayColor().CGColor
       
        if filterCheck == false{
            cell.itemCourse.text = arrCourseName[indexPath.section]
            cell.itemTopic.text = arrTopicName[indexPath.section]
            cell.itemVenue.text = arrLocation[indexPath.section]
            cell.itemParticipants.setTitle(String(arrParticipants[indexPath.section]), forState: .Normal)
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
            //cell.itemTime.text = arrStartTime[indexPath.section]
            //cell.itemDate.text = "Date Created: \(arrCreatedDate[indexPath.section])"
        }else{
            cell.itemCourse.text = arrFilterCourseName[indexPath.section]
            cell.itemTopic.text = arrFilterTopicName[indexPath.section]
            cell.itemVenue.text = arrFilterLocation[indexPath.section]
            cell.itemParticipants.setTitle(String(arrFilterParticipants[indexPath.section]), forState: .Normal)
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
            //cell.itemTime.text = arrFilterStartTime[indexPath.section]
            //cell.itemDate.text = "Date Created: \(arrFilterCreatedDate[indexPath.section])"
        }
        if checkCreated == false {
            cell.itemDate.hidden = false
            
            cell.btnInvite.hidden = false
            cell.btnDelete.hidden = false
            cell.itemImgInvite.hidden = false
            cell.itemImgDelete.hidden = false
            cell.btnInvite.addTarget(self, action: #selector(MyEventsVC.actionInvite(_:)), forControlEvents: .TouchUpInside)
            cell.btnDelete.addTarget(self, action: #selector(MyEventsVC.actionDelete(_:)), forControlEvents: .TouchUpInside)
            
            imgUrlString = defaults.stringForKey("PROFILE")!
            cell.itemUserName.text = defaults.stringForKey("PROFILENAME")
        }else{
            if filterCheck == false{
            imgUrlString = arrCUserPic[indexPath.section]
            cell.itemUserName.text = arrEventCUserName[indexPath.section]
            }else{
                imgUrlString = arrFilterCUserPic[indexPath.section]
                cell.itemUserName.text = arrFilterEventCUserName[indexPath.section]
            }
            //cell.itemDate.hidden = true
            
            cell.btnInvite.hidden = true
            cell.btnDelete.hidden = true
            cell.itemImgInvite.hidden = true
            cell.itemImgDelete.hidden = true
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
        cell.itemParticipants.addTarget(self, action: #selector(MyEventsVC.actionParticipants), forControlEvents: .TouchUpInside)
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                if error == nil {
                        imageCache[imgUrlString] = UIImage(data: data!)
                        cell.itemImg.image = UIImage(data: data!)
                }
        })*/
        return cell
    }
    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView?{
        let headerView = UIView(frame: CGRectMake(0, 0, tableView.bounds.size.width, 15))
        headerView.backgroundColor = UIColor.clearColor()
        return headerView
    }
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
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
    func actionInvite(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        let nextVC = self.storyboard?.instantiateViewControllerWithIdentifier("InviteContactsVC") as! InviteContactsVC
        if filterCheck == false{
            nextVC.courseName = arrCourseName[indexPath.section]
            nextVC.eventId = arrEventID[indexPath.section]
        }else{
            nextVC.courseName = arrFilterCourseName[indexPath.section]
            nextVC.eventId = arrEventID[indexPath.section]
        }
        self.navigationController?.pushViewController(nextVC, animated: true)
    }
    func actionDelete(sender: UIButton){
        let alert = UIAlertController(title: "Alert", message: "Are you sure you want to delete the selected study?", preferredStyle: .Alert)
        alert.addAction(UIAlertAction(title: "Yes", style: .Default){
            UIAlertAction in
            //remove
            let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
            NSLog("IndexPath: %li", indexPath.row)
            NSLog("IndexRow: %li", indexPath.section)
            var id = ""
             if self.filterCheck == false{
                id = self.arrEventID[indexPath.section]
             }else{
                id = self.arrFilterEventID[indexPath.section]
            }
            /*let data = [
                "eventid" : id,
                "delete" : ""
            ]*/
            self.showHUD()
            Alamofire.request(.POST, apiLink, parameters: [
                "eventid" : id,
                "delete" : ""
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
                                    }
                                self.arrEventID.removeAtIndex(indexPath.section)
                                self.arrCourseName.removeAtIndex(indexPath.section)
                                self.arrTopicName.removeAtIndex(indexPath.section)
                                self.arrStartTime.removeAtIndex(indexPath.section)
                                self.arrLocation.removeAtIndex(indexPath.section)
                                self.arrCreatedDate.removeAtIndex(indexPath.section)
                                self.arrParticipants.removeAtIndex(indexPath.section)
                                    
                                    
                                    self.tblAllEvents.reloadData()
                                    self.hideHUD()
                                    self.simpleAlert("Alert", message: "Study Has Been Deleted Successfully.")
                                }else{
                                    print("filterDelete")
                                    self.arrFilterEventID.removeAtIndex(indexPath.section)
                                    self.arrFilterCourseName.removeAtIndex(indexPath.section)
                                    self.arrFilterTopicName.removeAtIndex(indexPath.section)
                                    self.arrFilterStartTime.removeAtIndex(indexPath.section)
                                    self.arrFilterLocation.removeAtIndex(indexPath.section)
                                    self.arrFilterCreatedDate.removeAtIndex(indexPath.section)
                                    self.arrFilterParticipants.removeAtIndex(indexPath.section)
                                    for i in 0 ..< self.arrEventID.count {
                                        if self.arrEventID[i] == id{
                                            self.arrEventID.removeAtIndex(i)
                                            self.arrCourseName.removeAtIndex(i)
                                            self.arrTopicName.removeAtIndex(i)
                                            self.arrStartTime.removeAtIndex(i)
                                            self.arrLocation.removeAtIndex(i)
                                            self.arrCreatedDate.removeAtIndex(i)
                                            self.arrParticipants.removeAtIndex(i)
                                            break
                                        }
                                    }
                                    for i in 0 ..< arrDefaultEventID.count {
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
                                    }
                                    self.tblAllEvents.reloadData()
                                    self.hideHUD()
                                    self.simpleAlert("Alert", message: "Study Has Been Deleted Successfully.")
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
        
    }
    func getButtonIndexPath(button: UIButton) -> NSIndexPath {
        let buttonFrame: CGRect = button.convertRect(button.bounds, toView: tblAllEvents)
        return self.tblAllEvents.indexPathForRowAtPoint(buttonFrame.origin)!
    }
    @IBAction func actionCreated(sender: AnyObject) {
        self.viewAlert.hidden = true
        self.viewAlertC.hidden = true
        if checkCreated == true {
            arrEventID.removeAll()
            arrCourseName.removeAll()
            arrTopicName.removeAll()
            arrStartTime.removeAll()
            arrLocation.removeAll()
            arrCreatedDate.removeAll()
            arrParticipants.removeAll()
            arrEventCUserName.removeAll()
            arrCUserPic.removeAll()

            checkJoined = true
            checkCreated = false
            
            onceOnly = false
            
            myEventApi()
        }
        
        btnCreated.layer.borderColor = colorLightBlue.CGColor
        btnCreated.tintColor = colorLightBlue
        btnCreated.backgroundColor = UIColor.whiteColor()
        
        btnJoined.layer.borderColor = UIColor.whiteColor().CGColor
        btnJoined.tintColor = UIColor.whiteColor()
        btnJoined.backgroundColor = UIColor.clearColor()
    }
    @IBAction func actionJoined(sender: AnyObject) {
        self.viewAlert.hidden = true
        self.viewAlertC.hidden = true
        if checkJoined == true {
            arrEventID.removeAll()
            arrCourseName.removeAll()
            arrTopicName.removeAll()
            arrStartTime.removeAll()
            arrLocation.removeAll()
            arrCreatedDate.removeAll()
            arrParticipants.removeAll()
            arrEventCUserName.removeAll()
            arrCUserPic.removeAll()
            checkCreated = true
            checkJoined = false
            
            onceOnly = false
            
            joinedEventApi()
        }
        
        btnJoined.layer.borderColor = colorLightBlue.CGColor
        btnJoined.tintColor = colorLightBlue
        btnJoined.backgroundColor = UIColor.whiteColor()
        btnCreated.layer.borderColor = UIColor.whiteColor().CGColor
        btnCreated.tintColor = UIColor.whiteColor()
        btnCreated.backgroundColor = UIColor.clearColor()
    }
    func joinedEventApi(){
        /*let data = [
            "userid" : userID,
            "joineventlist" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "time_zone" : defaults.stringForKey("timeZone")!,
            "joineventlist" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let message = JSON["message"] as! NSArray
                            print(message)
                           
                            for i in 0 ..< message.count {
                                if let userid = message[i]["userid"] as? String {
                                    if userid != userID{
                                        if let event_id = message[i]["event_id"] as? String {
                                            self.arrEventID.append(event_id)
                                        }
                                        if let  name = message[i]["Course_Name"] as? String {
                                            self.arrCourseName.append(name)
                                        }
                                        if let topicname = message[i]["topic"] as? String {
                                            self.arrTopicName.append(topicname)
                                        }
                                        if let starttime = message[i]["starttime"] as? String {
                                            self.arrStartTime.append(starttime)
                                        }
                                        if let location = message[i]["location"] as? String {
                                            self.arrLocation.append(location)
                                        }
                                        if let createdDate = message[i]["eventdate"] as? String {
                                            self.arrCreatedDate.append(createdDate)
                                        }
                                        if let participants = message[i]["participants"] as? Int{
                                            self.arrParticipants.append(participants)
                                        }
                                        if let fname = message[i]["fname"] as? String {
                                            if let lname = message[i]["lname"] as? String{
                                                self.arrEventCUserName.append("\(fname) \(lname)")
                                            }
                                        }
                                        if let userpic = message[i]["userpic"] as? String {
                                            self.arrCUserPic.append(userpic)
                                        }

                                    }
                                   
                                }
                            }//end for loop
                            print(self.arrTopicName)
                            print(self.arrEventCUserName)
                            print(self.arrLocation)
                            print(self.arrStartTime)
                            //print(self.arrCourseName)
                            //print(self.arrCUserPic)
                           // print(self.arrParticipants)
                            self.pressed(self.cancelButton)
                            self.tblAllEvents.reloadData()
                            self.hideHUD()
                        }else{
                            self.viewAlert.hidden = false
                            self.hideHUD()
                            self.pressed(self.cancelButton)
                            self.tblAllEvents.reloadData()
                            // self.simpleAlert("Alert", message: "No Class Study Joined.")
                        }
                    }
                }else{
                    self.hideHUD()
                    self.pressed(self.cancelButton)
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
        
        print("joineventlist")
    }
    func myEventApi(){
       
        /*let data = [
            "userid" : userID,
            "myeventlist" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "time_zone" : defaults.stringForKey("timeZone")!,
            "myeventlist" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                             self.pressed(self.cancelButton)
                            let message = JSON["message"] as! NSArray
                            print(message)
                            for i in 0 ..< message.count {
                                if let eventid = message[i]["eventid"] as? String {
                                    self.arrEventID.append(eventid)
                                }
                                if let  name = message[i]["name"] as? String {
                                 self.arrCourseName.append(name)
                                 }
                                 if let topicname = message[i]["topicname"] as? String {
                                 self.arrTopicName.append(topicname)
                                 }
                                 if let starttime = message[i]["starttime"] as? String {
                                 self.arrStartTime.append(starttime)
                                 }
                                 if let location = message[i]["location"] as? String {
                                 self.arrLocation.append(location)
                                 }
                                 if let participants = message[i]["participants"] as? Int {
                                 self.arrParticipants.append(participants)
                                 }
                                 if let created = message[i]["created"] as? String {
                                 self.arrCreatedDate.append(created)
                                 }
                            }//end for loop
                            
                            self.tblAllEvents.reloadData()
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            self.pressed(self.cancelButton)
                            self.viewAlertC.hidden = false
                            //self.simpleAlert("Alert", message: "No Class Study Found.")
                        }
                    }
                }else{
                    self.hideHUD()
                    self.pressed(self.cancelButton)
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
    }
    func actionSort(){
        // let a = ["hello","i","BA3","BA21","going","ba12","going"]
        
        //let idxs = sorted(indices(a)) { a[$0] < a[$1] }
        // print(idxs)
        //self.arrSortUserName = arrUserName.sort(){ $0 < $1 }
        //print(arrSortUserName)
        if !onceOnly {
            
            arrDefaultCourseName = arrCourseName
            arrDefaultTopicName = arrTopicName
            arrDefaultStartTime = arrStartTime
            arrDefaultLocation = arrLocation
            arrDefaultParticipants = arrParticipants
            arrDefaultCreatedDate = arrCreatedDate
            arrDefaultEventID = arrEventID
            if checkCreated == true {
                arrDefaultEventCUserName = arrEventCUserName
                arrDefaultCUserPic = arrCUserPic
            }
            //arrDefaultEventCUserID = arrEventCUserID
           // arrDefaultStatus = arrStatus
            onceOnly = true
        }
       
        arrSortCourseName.removeAll()
        arrSortTopicName.removeAll()
        arrSortStartTime.removeAll()
        arrSortLocation.removeAll()
        arrSortParticipants.removeAll()
        arrSortCreatedDate.removeAll()
        arrSortEventID.removeAll()
        if checkCreated == true {
            arrSortUserName.removeAll()
            arrSortPic.removeAll()
        }
        //arrSortEventCUserID.removeAll()
       // arrSortStatus.removeAll()
        
        
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
                
                arrSortCourseName.append(self.arrCourseName[key])
                arrSortTopicName.append(self.arrTopicName[key])
                arrSortStartTime.append(self.arrStartTime[key])
                arrSortLocation.append(self.arrLocation[key])
                arrSortParticipants.append(self.arrParticipants[key])
                arrSortCreatedDate.append(self.arrCreatedDate[key])
                arrSortEventID.append(self.arrEventID[key])
                if self.checkCreated == true {
                    arrSortUserName.append(self.arrEventCUserName[key])
                    arrSortPic.append(self.arrCUserPic[key])
                }
                //arrSortEventCUserID.append(self.arrEventCUserID[key])
                //arrSortStatus.append(self.arrStatus[key])
            }
            self.arrCourseName = arrSortCourseName
            self.arrTopicName = arrSortTopicName
            self.arrStartTime = arrSortStartTime
            self.arrLocation = arrSortLocation
            self.arrParticipants = arrSortParticipants
            self.arrCreatedDate = arrSortCreatedDate
            self.arrEventID = arrSortEventID
            if self.checkCreated == true {
                self.arrEventCUserName = arrSortUserName
                self.arrCUserPic = arrSortPic
            }
            //self.arrEventCUserID = arrSortEventCUserID
            //self.arrStatus = arrSortStatus
            
            self.tblAllEvents.reloadData()
            self.hideHUD()
        }
        let twoAction = UIAlertAction(title: "Date & Time", style: .Default) { (action) in
            self.pressed(self.cancelButton)
            self.arrCourseName = arrDefaultCourseName
            self.arrTopicName = arrDefaultTopicName
            self.arrStartTime = arrDefaultStartTime
            self.arrLocation = arrDefaultLocation
            self.arrParticipants = arrDefaultParticipants
            self.arrCreatedDate = arrDefaultCreatedDate
            self.arrEventID = arrDefaultEventID
            if self.checkCreated == true {
                self.arrEventCUserName = arrDefaultEventCUserName
                self.arrCUserPic = arrDefaultCUserPic
            }
            // self.arrEventCUserID = arrDefaultEventCUserID
            //self.arrStatus = arrDefaultStatus
            self.tblAllEvents.reloadData()
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