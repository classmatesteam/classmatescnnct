//
//  ParticipantsListVC.swift
//  Classmates
//
//  Created by MAC on 7/28/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import Alamofire
class ParticipantsListVC: UIViewController {
    
    @IBOutlet weak var tblParticipants: UITableView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    
    var arrUserName = [String]()
    var arrPic = [String]()
    var eventID = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
        setGestures()
    }
    func configureDesign(){
        tblParticipants.rowHeight = 70.0
        tblParticipants.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        tblParticipants.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        print(eventID)
        /*let data = [
            "eventid" : eventID,
            "participantslist" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "eventid" : eventID,
            "participantslist" : ""
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
                        
                                 if let fname = message[i]["fname"] as? String {
                                    if let lname = message[i]["lname"] as? String{
                                        self.arrUserName.append("\(fname) \(lname)")
                                    }
                                 }
                                if let userpic = message[i]["userpic"] as? String {
                                    self.arrPic.append(userpic)
                                }
                                
                            }//end for loop
                            self.hideHUD()
                        }else{
                            self.hideHUD()
                            //self.simpleAlert("Alert", message: JSON["message"] as! String)
                        }
                    }
                    self.tblParticipants.reloadData()
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
        
    }
    func setGestures(){
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ParticipantsListVC.actionBack)))
    }
    //MARK:TableView Delegate Methods
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int{
        return self.arrUserName.count
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> ParticipantsTableViewCell{
        let cell = tblParticipants.dequeueReusableCellWithIdentifier("participantsId") as! ParticipantsTableViewCell
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
        
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
         NSURLConnection.sendAsynchronousRequest(
         request, queue: NSOperationQueue.mainQueue(),
         completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
         if error == nil {
         cell.itemImg.image = UIImage(data: data!)
         }
         })*/
        cell.itemName.text = arrUserName[indexPath.row]
        return cell
    }
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}