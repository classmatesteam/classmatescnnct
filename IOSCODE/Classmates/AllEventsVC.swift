//
//  AllEventsVC.swift
//  Classmates
//
//  Created by Mallu on 6/22/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
class AllEventsVC: UIViewController, UITableViewDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var tblAllEvents: UITableView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var viewSort: UIView!
    @IBOutlet weak var viewFilter: UIView!
    
    @IBOutlet weak var viewAlert: UIView!
    @IBOutlet weak var btnAddStudy: UIButton!
    @IBOutlet weak var lblAlert: UILabel!
    
    var arrEventID = [String]()
    var arrCourseName = [String]()
    var arrTopicName = [String]()
    var arrStartTime = [String]()
    var arrLocation = [String]()
    var arrUserName = [String]()
    var arrPic = [String]()
    var arrParticipants = [Int]()
    var arrCreatedDate = [String]()
    var arrEventCUserID = [String]()
    var arrStatus = [String]()
    
    //var userID = ""
    var courseName = ""
    var onceOnly = false
    //filter array(11)
    var arrFilterEventID = [String]()
    var arrFilterCourseName = [String]()
    var arrFilterTopicName = [String]()
    var arrFilterStartTime = [String]()
    var arrFilterLocation = [String]()
    var arrFilterUserName = [String]()
    var arrFilterPic = [String]()
    var arrFilterParticipants = [Int]()
    var arrFilterCreatedDate = [String]()
    var arrFilterEventCUserID = [String]()
    var arrFilterStatus = [String]()
    
    
    var textField = UITextField()
    let cancelButton = UIButton()
    var filterCheck = false
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
    }
    func configureDesign(){
        print(courseName)
        viewAlert.hidden = true
        lblTitle.text = courseName
        userID = defaults.stringForKey("userID")!
        tblAllEvents.rowHeight = 165
        //initAppearNavigation(viewTop)
        tblAllEvents.separatorStyle = .None
        tblAllEvents.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblAllEvents.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(AllEventsVC.actionBack)))
        /*let data = [
            "name" : courseName,
            "userid": userID,
            "searchevent" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "name" : courseName,
            "userid": userID,
            "time_zone" : defaults.stringForKey("timeZone")!,
            "searchevent" : ""
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
                                    self.arrEventCUserID.append(userid)
                                    if userid != userID{
                                    if let join = message[i]["join"] as? String {
                                        self.arrStatus.append(join)
                                    }
                                    }else{
                                        self.arrStatus.append("YesOwn")
                                    }
                                }
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
                                if let createdDate = message[i]["eventdate"] as? String {
                                    self.arrCreatedDate.append(createdDate)
                                }
                                if let participants = message[i]["participants"] as? Int {
                                    self.arrParticipants.append(participants)
                                }
                                if let userpic = message[i]["userpic"] as? String {
                                    self.arrPic.append(userpic)
                                }
                               
                                if let eventid = message[i]["eventid"] as? String{
                                    self.arrEventID.append(eventid)
                                }
                            }//end for loop
                            self.tblAllEvents.reloadData()
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            //self.btnAddStudy.tintColor = colorPink
                            //self.btnAddStudy.layer.borderWidth = 2.5
                            //self.btnAddStudy.layer.borderColor = colorPink.CGColor
                            //self.btnAddStudy.layer.cornerRadius = self.btnAddStudy.frame.size.width/2.0
                            //self.btnAddStudy.clipsToBounds = true
                            self.tblAllEvents.hidden = true
                            self.viewAlert.hidden = false
                            
                            self.lblAlert.text = "NO STUDY FOUND FOR \(self.courseName)"
                            self.lblAlert.adjustsFontSizeToFitWidth = true
                            myMutableString = NSMutableAttributedString(string: self.lblAlert.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 19.0)!])
                            myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorPink, range: NSRange(location:18,length:7))
                            self.lblAlert.attributedText = myMutableString
                            //self.actionAddStudy(self.btnAddStudy)
                           // self.simpleAlert("Alert", message: "No Class Study Found!")
                        }
                    }
                    
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
        
        
        //filter table
        self.viewFilter.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(AllEventsVC.actionFilter)))
        
        textField = UITextField(frame: CGRectMake(tblAllEvents.frame.origin.x, tblAllEvents.frame.origin.y + 10, tblAllEvents.frame.size.width - 70, 32.0))
        textField.attributedPlaceholder = NSAttributedString(string: "Search User Name", attributes: [NSForegroundColorAttributeName: colorLightBlue])
        filterTextField(textField)
        //textField.autocapitalizationType = UITextAutocapitalizationType.AllCharacters
        
        filterButton(cancelButton)
        cancelButton.frame = CGRectMake(textField.frame.origin.x + textField.frame.size.width + 10, textField.frame.origin.y, 60, 32)
        cancelButton.addTarget(self, action: #selector(AllEventsVC.pressed(_:)), forControlEvents: .TouchUpInside)
        textField.delegate = self
        
        //sort
         self.viewSort.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(AllEventsVC.actionSort)))
    }
    
    @IBAction func actionAddStudy(sender: AnyObject) {
        let nextVC = self.storyboard?.instantiateViewControllerWithIdentifier("CourseVC") as! CourseVC
        self.navigationController?.pushViewController(nextVC, animated: true)
        nextVC.arrClassList.append(courseName)
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
        tblAllEvents.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblAllEvents.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        tblAllEvents.reloadData()
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    func textField(textField: UITextField, shouldChangeCharactersInRange range: NSRange, replacementString string: String) -> Bool {
        
        filter((textField.text! as NSString).stringByReplacingCharactersInRange(range, withString: string))
        return true
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
        arrFilterCreatedDate.removeAll()
        arrFilterEventCUserID.removeAll()
        arrFilterStatus.removeAll()
        //print(textField.text)
        for i in 0 ..< arrEventID.count {
            if (arrUserName[i].lowercaseString).containsString(text.lowercaseString){
                arrFilterEventID.append(arrEventID[i])
                arrFilterCourseName.append(arrCourseName[i])
                arrFilterTopicName.append(arrTopicName[i])
                arrFilterStartTime.append(arrStartTime[i])
                arrFilterLocation.append(arrLocation[i])
                arrFilterUserName.append(arrUserName[i])
                arrFilterPic.append(arrPic[i])
                arrFilterParticipants.append(arrParticipants[i])
                arrFilterCreatedDate.append(arrCreatedDate[i])
                arrFilterEventCUserID.append(arrEventCUserID[i])
                arrFilterStatus.append(arrStatus[i])
            }
        }
        filterCheck = true
        tblAllEvents.reloadData()
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
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> AllEventsTableViewCell{
        let cell = tblAllEvents.dequeueReusableCellWithIdentifier("allEventsId") as! AllEventsTableViewCell
        cell.itemDate.text = ""
        if filterCheck == false{
            cell.itemCourse.text = arrCourseName[indexPath.section]
            cell.itemTopic.text = arrTopicName[indexPath.section]
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
            cell.itemVenue.text = arrLocation[indexPath.section]
             cell.itemParticipants.setTitle(String(arrParticipants[indexPath.section]), forState: .Normal)
            
            //cell.itemDate.text = "Date Created: \(arrCreatedDate[indexPath.section])"
            cell.itemUserName.text = arrUserName[indexPath.section]
            if arrStatus[indexPath.section] == "Yes"{
                cell.btnJoin.setTitle("UNJOIN", forState: .Normal)
                cell.btnJoin.enabled = true
            }else if arrStatus[indexPath.section] == "YesOwn"{
                cell.btnJoin.setTitle("JOINED", forState: .Normal)
                cell.btnJoin.enabled = false
            }else {
                cell.btnJoin.setTitle("JOIN", forState: .Normal)
                cell.btnJoin.enabled = true
            }
            imgUrlString = arrPic[indexPath.section]
            
        }else{
            cell.itemCourse.text = arrFilterCourseName[indexPath.section]
            cell.itemTopic.text = arrFilterTopicName[indexPath.section]
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
            cell.itemVenue.text = arrFilterLocation[indexPath.section]
             cell.itemParticipants.setTitle(String(arrFilterParticipants[indexPath.section]), forState: .Normal)
            
            //cell.itemDate.text = "Date Created: \(arrFilterCreatedDate[indexPath.section])"
            cell.itemUserName.text = arrFilterUserName[indexPath.section]
            if arrFilterStatus[indexPath.section] == "Yes"{
                cell.btnJoin.setTitle("UNJOIN", forState: .Normal)
                cell.btnJoin.enabled = true
            }else if arrFilterStatus[indexPath.section] == "YesOwn"{
                cell.btnJoin.setTitle("JOINED", forState: .Normal)
                cell.btnJoin.enabled = false
            }else{
                cell.btnJoin.setTitle("JOIN", forState: .Normal)
                cell.btnJoin.enabled = true
            }
            imgUrlString = arrFilterPic[indexPath.section]
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
        cell.btnJoin.addTarget(self, action: #selector(AllEventsVC.actionJoin(_:)), forControlEvents: .TouchUpInside)
        cell.itemParticipants.addTarget(self, action: #selector(AllEventsVC.actionParticipants), forControlEvents: .TouchUpInside)
        cell.layer.cornerRadius = 10
        cell.layer.borderWidth = 2.0
        cell.layer.borderColor = UIColor.lightGrayColor().CGColor
        //cell.itemViewTop.backgroundColor = colorLightBlue
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
    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView?{
        let headerView = UIView(frame: CGRectMake(0, 0, tableView.bounds.size.width, 15))
        headerView.backgroundColor = UIColor.clearColor()
        return headerView
    }
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    func getButtonIndexPath(button: UIButton) -> NSIndexPath {
        let buttonFrame: CGRect = button.convertRect(button.bounds, toView: tblAllEvents)
        return self.tblAllEvents.indexPathForRowAtPoint(buttonFrame.origin)!
    }
    func actionJoin(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        var eventId = ""
        var createdUserId = ""
        var joinStatus = ""
        if filterCheck == false{
            eventId = arrEventID[indexPath.section]
            createdUserId = arrEventCUserID[indexPath.section]
            joinStatus = arrStatus[indexPath.section]
        }else{
            eventId = arrFilterEventID[indexPath.section]
            createdUserId = arrFilterEventCUserID[indexPath.section]
            joinStatus = arrFilterStatus[indexPath.section]
        }
        print(eventId)
        print(createdUserId)
    
        /*let data = [
            "userid" : createdUserId,
            "eventid" : eventId,
            "friendsid": userID,
            "join" : ""
        ]*/
        self.showHUD()
        if joinStatus == "No" {
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
                                    self.arrStatus[indexPath.section] = "Yes"
                                    self.arrParticipants[indexPath.section] = self.arrParticipants[indexPath.section] + 1
                                }else{
                                    self.arrFilterStatus[indexPath.section] = "Yes"
                                    self.arrFilterParticipants[indexPath.section] = self.arrFilterParticipants[indexPath.section] + 1
                                    for i in 0 ..< self.arrEventID.count {
                                        if self.arrEventID[i] == eventId{
                                            self.arrStatus[i] = "Yes"
                                            self.arrParticipants[indexPath.section] = self.arrParticipants[indexPath.section] + 1
                                        }
                                    }// end for loop
                                    for i in 0 ..< arrDefaultEventID.count {
                                        if arrDefaultEventID[i] == eventId{
                                            arrDefaultStatus[i] = "Yes"
                                            arrDefaultParticipants[indexPath.section] = arrDefaultParticipants[indexPath.section] + 1
                                        }
                                    }// end for loop
                                }
                                self.tblAllEvents.reloadData()
                                self.hideHUD()
                                self.alertToMsg("You Have Joined This Study Successfully.")
                                // self.simpleAlert("Alert", message: "You have joined this study successfully.")
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
        }else {
            Alamofire.request(.POST, apiLink, parameters: [
                "userid" : userID,
                "eventid" : eventId,
                "reject" : ""
                ])
                .responseJSON {response in
                    //print(response.result)
                    if String(response.result) == "SUCCESS"{
                        
                        if let JSON = response.result.value {
                            print("JSON: \(JSON)")
                            apiStatus = JSON["status"] as! String
                            if apiStatus == "success" {
                               
                                if self.filterCheck == false{
                                    self.arrStatus[indexPath.section] = "No"
                                    self.arrParticipants[indexPath.section] = self.arrParticipants[indexPath.section] - 1
                                }else{
                                    self.arrFilterStatus[indexPath.section] = "No"
                                    self.arrFilterParticipants[indexPath.section] = self.arrFilterParticipants[indexPath.section] - 1
                                    for i in 0 ..< self.arrEventID.count {
                                        if self.arrEventID[i] == eventId{
                                            self.arrStatus[i] = "No"
                                            self.arrParticipants[indexPath.section] = self.arrParticipants[indexPath.section] - 1
                                        }
                                    }// end for loop
                                    for i in 0 ..< arrDefaultEventID.count {
                                        if arrDefaultEventID[i] == eventId{
                                            arrDefaultStatus[i] = "No"
                                            arrDefaultParticipants[indexPath.section] = arrDefaultParticipants[indexPath.section] - 1
                                        }
                                    }// end for loop
                                }
                                self.tblAllEvents.reloadData()
                                self.hideHUD()
                                self.simpleAlert("Alert", message: "You Have Unjoined This Study Successfully.")
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
        
    }
    func actionSort(){
       // let a = ["hello","i","BA3","BA21","going","ba12","going"]
       
        //let idxs = sorted(indices(a)) { a[$0] < a[$1] }
       // print(idxs)
        //self.arrSortUserName = arrUserName.sort(){ $0 < $1 }
        //print(arrSortUserName)
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
        arrDefaultStatus = arrStatus
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
        arrSortStatus.removeAll()
        
        
        var dictOfIntegers = [Int: String]()
       
        for i in 0 ..< arrUserName.count {
            dictOfIntegers[i] = arrUserName[i]
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
        let oneAction = UIAlertAction(title: "Date & Time", style: .Default) { (action) in
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
            self.arrStatus = arrDefaultStatus
            
            self.tblAllEvents.reloadData()
        }
        let twoAction = UIAlertAction(title: "User Name", style: .Default) { (action) in
            
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
                arrSortStatus.append(self.arrStatus[key])
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
            self.arrStatus = arrSortStatus
            
            self.tblAllEvents.reloadData()
            self.hideHUD()
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
//http://stackoverflow.com/questions/33882057/swift-sort-dictionary-keys-by-value-then-by-key
