//
//  ParticipantsVC.swift
//  Classmates
//
//  Created by Mallu on 6/18/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
class AcceptInviteVC: UIViewController, UITableViewDelegate{
    
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var tblAcceptInvite: UITableView!
    //var userID = ""
    var arrEventCUserId = [String]()
    var arrEventCUserName = [String]()
    var arrInviteID = [String]()
    var arrCourseName = [String]()
    var arrTopicName = [String]()
    var arrFriendsImg = [String]()
    var arrFriendName = [String]()
    var arrEventId = [String]()
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
        
    }
    func configureDesign(){
        tblAcceptInvite.rowHeight = 70.0
        tblAcceptInvite.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblAcceptInvite.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(AcceptInviteVC.actionBack)))
        userID = defaults.stringForKey("userID")!
        /*let data = [
            "userid" : userID,
            "invitecount" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "invitecount" : ""
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
                                
                                if let eventId = message[i]["event_id"] as? String {
                                    self.arrEventId.append(eventId)
                                }
                                if let inviteid = message[i]["inviteid"] as? String {
                                    self.arrInviteID.append(inviteid)
                                }
                                if let  name = message[i]["name"] as? String {
                                    self.arrCourseName.append(name)
                                }
                                if let topicname = message[i]["topicname"] as? String {
                                    self.arrTopicName.append(topicname)
                                }
                                if let userid = message[i]["userid"] as? String {
                                    self.arrEventCUserId.append(userid)
                                }
                                if let  fname = message[i]["fname"] as? String {
                                    if let  lname = message[i]["lname"] as? String {
                                        self.arrFriendName.append("\(fname) \(lname)")
                                    }
                                }
                                if let  userpic = message[i]["userpic"] as? String {
                                    self.arrFriendsImg.append(userpic)
                                }
                            }//end for loop
                            self.tblAcceptInvite.reloadData()
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            //self.simpleAlert("Alert", message: "Sorry, No Invite Found.")
                        }
                    }
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
    }
    
    //MARK:TableView Delegate Methods
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return self.arrInviteID.count
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> AcceptInviteTableViewCell{
        let cell = tblAcceptInvite.dequeueReusableCellWithIdentifier("acceptInviteId") as! AcceptInviteTableViewCell
        
        imgUrlString = arrFriendsImg[indexPath.row]
        if imgUrlString.containsString("http"){
            ImageLoader.sharedLoader.imageForUrl(imgUrlString, completionHandler:{(image: UIImage?, url: String) in
                cell.itemUserImage.image = image
            })
        }else{
            ImageLoader.sharedLoader.imageForUrl("\(imgApiLink)\(imgUrlString)", completionHandler:{(image: UIImage?, url: String) in
                cell.itemUserImage.image = image
            })
        }
        cell.itemCourseName.text = "\(arrCourseName[indexPath.row])(\(arrTopicName[indexPath.row]))"
        cell.itemUserName.text = arrFriendName[indexPath.row]
        cell.btnAccept.addTarget(self, action: #selector(AcceptInviteVC.actionAccept(_:)), forControlEvents: .TouchUpInside)
        cell.btnReject.addTarget(self, action: #selector(AcceptInviteVC.actionReject(_:)), forControlEvents: .TouchUpInside)
        return cell
    }
    
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    func actionAccept(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        callInviteApi(indexPath)
    }
    func actionReject(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        callRejectApi(indexPath)
    }
    func getButtonIndexPath(button: UIButton) -> NSIndexPath {
        let buttonFrame: CGRect = button.convertRect(button.bounds, toView: tblAcceptInvite)
        return self.tblAcceptInvite.indexPathForRowAtPoint(buttonFrame.origin)!
    }
    func callInviteApi(index: NSIndexPath){
        print(index)
        print(index.row)
        /*let data = [
            "inviteid" : arrInviteID[index.row],
            "friendsid" : arrEventCUserId[index.row],
            "accept" : ""
        ]*/
        self.showHUD()
       
        Alamofire.request(.POST, apiLink, parameters: [
            "inviteid" : arrEventId[index.row],
            //"friendsid" : arrEventCUserId[index.row],
            "friendsid" : userID,
            "accept" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            //let message = JSON["message"] as! String
                            //self.simpleAlert("Alert", message: JSON["message"] as! String)
                            self.arrEventId.removeAtIndex(index.row)
                            self.arrInviteID.removeAtIndex(index.row)
                            self.arrEventCUserId.removeAtIndex(index.row)
                            self.arrTopicName.removeAtIndex(index.row)
                            self.arrCourseName.removeAtIndex(index.row)
                            self.arrFriendName.removeAtIndex(index.row)
                            self.arrFriendsImg.removeAtIndex(index.row)
                            self.tblAcceptInvite.reloadData()
                            self.hideHUD()
                            self.alertToMsg("Invitation Accepted Successfully.")
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
    func callRejectApi(index: NSIndexPath){
        print(index)
        print(index.row)
        /*let data = [
         "inviteid" : arrInviteID[index.row],
         "friendsid" : arrEventCUserId[index.row],
         "accept" : ""
         ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "eventid" : arrEventId[index.row],
            "reject" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            //let message = JSON["message"] as! String
                            //self.simpleAlert("Alert", message: JSON["message"] as! String)
                            self.arrEventId.removeAtIndex(index.row)
                            self.arrInviteID.removeAtIndex(index.row)
                            self.arrEventCUserId.removeAtIndex(index.row)
                            self.arrTopicName.removeAtIndex(index.row)
                            self.arrCourseName.removeAtIndex(index.row)
                            self.arrFriendName.removeAtIndex(index.row)
                            self.arrFriendsImg.removeAtIndex(index.row)
                            self.tblAcceptInvite.reloadData()
                            self.hideHUD()
                            self.simpleAlert("Alert", message: "Invitation Has Been Rejected Successfully.")
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
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}