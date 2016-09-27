//
//  InviteContactsVC.swift
//  Classmates
//
//  Created by Mallu on 6/28/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
 import Alamofire
class InviteContactsVC: UIViewController, UITableViewDelegate{
    
    @IBOutlet weak var tblInviteContacts: UITableView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    //var userID = ""
    var courseName = ""
    var arrFriendsID = [String]()
    //var arrEventID = [String]()
    var eventId = ""
    var arrFriendsImg = [String]()
    var arrFriendName = [String]()
    var arrInviteStatus = [String]()
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
        print(courseName)
    }
    func configureDesign(){
        tblInviteContacts.rowHeight = 70.0
        tblInviteContacts.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblInviteContacts.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(InviteContactsVC.actionBack)))
        userID = defaults.stringForKey("userID")!
        /*let data = [
            "userid" : userID,
            "name" : courseName,
            "eventid" : eventId,
            "contactlist" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "name" : courseName,
            "eventid" : eventId,
            "contactlist" : ""
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
                               
                               /* if let  eventID = message[i]["eventid"] as? String {
                                    self.arrEventID.append(eventID)
                                }*/
                                if let  userid = message[i]["userid"] as? String {
                                    self.arrFriendsID.append(userid)
                                }
                                if let  fname = message[i]["fname"] as? String {
                                    if let  lname = message[i]["lname"] as? String {
                                        self.arrFriendName.append("\(fname) \(lname)")
                                    }
                                }
                                if let  userpic = message[i]["userpic"] as? String {
                                    self.arrFriendsImg.append(userpic)
                                }
                                if let  invite = message[i]["invite"] as? String {
                                    self.arrInviteStatus.append(invite)
                                }
                            }//end for loop
                            self.tblInviteContacts.reloadData()
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            //self.simpleAlert("Alert", message: "Sorry, Contact List Not Found.")
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
        return self.arrFriendName.count
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> InviteContactsTableViewCell{
        let cell = tblInviteContacts.dequeueReusableCellWithIdentifier("inviteContactsId") as! InviteContactsTableViewCell
        imgUrlString = arrFriendsImg[indexPath.row]
        
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
        if arrInviteStatus[indexPath.row] == "Yes"{
            cell.btnInvite.setTitle("Invited", forState: .Normal)
            cell.btnInvite.enabled = false
        }else{
            cell.btnInvite.setTitle("Invite", forState: .Normal)
            cell.btnInvite.enabled = true
        }
        cell.itemName.text = arrFriendName[indexPath.row]
        cell.btnInvite.addTarget(self, action: #selector(InviteContactsVC.actionInvite(_:)), forControlEvents: .TouchUpInside)
        return cell
    }
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    func actionInvite(sender: UIButton){
        let indexPath: NSIndexPath = self.getButtonIndexPath(sender)
        NSLog("IndexPath: %li", indexPath.row)
        NSLog("IndexRow: %li", indexPath.section)
        callInviteApi(indexPath)
    }
    func getButtonIndexPath(button: UIButton) -> NSIndexPath {
        let buttonFrame: CGRect = button.convertRect(button.bounds, toView: tblInviteContacts)
        return self.tblInviteContacts.indexPathForRowAtPoint(buttonFrame.origin)!
    }
    func callInviteApi(index: NSIndexPath){
        print(index)
        print(index.row)
        /*let data = [
            "userid" : userID,
            "eventid" : eventId,
            "friendsid" : arrFriendsID[index.row],
            "invite" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "eventid" : eventId,
            "friendsid" : arrFriendsID[index.row],
            "invite" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let message = JSON["message"] as! String
                            print(message)
                            self.simpleAlert("Alert", message: "Invitation Has Been Sent Successfully.")
                            self.arrInviteStatus[index.row] = "Yes"
                            //self.arrFriendsID.removeAtIndex(index.row)
                            ////self.arrEventID.removeAtIndex(index.row)
                            //self.arrFriendsImg.removeAtIndex(index.row)
                            //self.arrFriendName.removeAtIndex(index.row)
                            //self.arrInviteStatus.removeAtIndex(index.row)
                            self.tblInviteContacts.reloadData()
                            self.hideHUD()
                        }else{
                            self.hideHUD()
//                            self.simpleAlert("Alert", message: JSON["message"] as! String)
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