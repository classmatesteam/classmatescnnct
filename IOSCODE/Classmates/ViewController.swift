//
//  ViewController.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import UIKit
import FBSDKLoginKit
import FBSDKCoreKit
import SafariServices
import Alamofire
class ViewController: UIViewController, SFSafariViewControllerDelegate {

    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var btnLogin: UIButton!
    var name = ""
    var imgUrl = ""
    var deviceID = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        
       configureDesign()
        
    }
    func configureDesign(){
        //imgTitle.frame.origin.x = lblTitle.frame.origin.x + lblTitle.frame.size.width - 22
       
        myMutableString = NSMutableAttributedString(string: lblTitle.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 18.0)!])
        
        myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorPink, range: NSRange(location:11,length:5))
        myMutableString.addAttribute(NSForegroundColorAttributeName, value: UIColor.whiteColor(), range: NSRange(location:16,length:6))
        lblTitle.attributedText = myMutableString
       
        
    }
    override func viewWillAppear(animated: Bool) {
       // self.navigationController?.navigationBarHidden = true
    }
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(true)
    }
    @IBAction func actionLogin(sender: AnyObject) {
        let fbLoginManager : FBSDKLoginManager = FBSDKLoginManager()
        fbLoginManager.logInWithReadPermissions(["email"], handler: { (result, error) -> Void in
            
            
            if  (result.isCancelled) {
               // print("cancelled")
            }else{
                self.getFBUserData()
            }
        })
        //let safariVC = SFSafariViewController(URL:NSURL(string: "https://m.facebook.com/login.php?next")!, entersReaderIfAvailable: true)
        //safariVC.delegate = self
        //self.presentViewController(safariVC, animated: true, completion: nil)
    }
    func getFBUserData(){
        if((FBSDKAccessToken.currentAccessToken()) != nil){
            FBSDKGraphRequest(graphPath: "me", parameters: ["fields": "id, name, first_name, last_name, picture.type(large), email"]).startWithCompletionHandler({ (connection, result, error) -> Void in
                if (error == nil){
                    print(result)
                    let resultdict = result as! NSDictionary
                    print("Result Dict: \(resultdict)")
                    self.name = resultdict["name"] as! String
                    let id: String = resultdict["id"] as! String
                    let fName: String = resultdict["first_name"] as! String
                    let lName: String = resultdict["last_name"] as! String
                    //if resultdict["picture"] == "data"
                    self.imgUrl = "https://graph.facebook.com/\(id)/picture?type=large"
                    //"f7486215ea450d8c51c578c67f4a62f79480bac99e7ad594be0d2a332bba79e2"
                    //1154111434661636
                    print(self.name)
                    print(fName)
                    print(lName)
                    print(id)
                    print(self.imgUrl)
                    print(modelName)
                    //e5f5a13db0a7f70cd666c62fb5c510f1fb2d5c4d034b3a3e95368a2a14e5388d
                   // print(id)
                   // print(email)
                    //print(picture)
                   // print(self.deviceID)
                    if defaults.stringForKey("DeviceToken") != nil{
                         self.deviceID = defaults.stringForKey("DeviceToken")!
                    }else{
                        self.deviceID = "f7486215ea450d8c51c578c67f4a62f79480bac99e7ad594be0d2a332bba79e2"
                    }
                    self.showHUD()
                    Alamofire.request(.POST, apiLink, parameters: [
                        "Fname" : fName as String,
                        "Lname" : lName as String,
                        "Image" : self.imgUrl as String,
                        "access_token" : id,
                        "DEVICE_ID" : self.deviceID,
                        "DEVICE_TYPE" : modelName,
                        "Fb_login" : ""
                        ])
                        .responseJSON {response in
                            //print(response.result)
                            if String(response.result) == "SUCCESS"{
                                print(response.result.value)
                                if let JSON = response.result.value {
                                    print("JSON: \(JSON)")
                                    apiStatus = JSON["status"] as! String
                                    
                                    if apiStatus == "success" {
                                        let msg = JSON["msg"] as! NSDictionary
                                        let userid = msg["userid"] as! String
                                        let count = msg["count"] as! String
                                        print(count)
                                        print(userid)
                                        defaults.setObject(userid, forKey: "userID")
                                    
                                        
                                        if let  userpic = msg["userpic"] as? String {
                                            print(userpic)
                                            defaults.setObject(userpic, forKey: "PROFILE")
                                        }
                                        if let fname = msg["fname"] as? String {
                                            if let lname = msg["lname"] as? String{
                                                defaults.setObject("\(fname) \(lname)", forKey: "PROFILENAME")
                                            }
                                        }
                                        
                                        self.hideHUD()
                                        
                                        
                                        if count == "0"{
                                            self.performSegueWithIdentifier("segueLogin", sender: self)
                                        }else{
                                             self.navigationController?.pushViewController(self.storyboard!.instantiateViewControllerWithIdentifier("DialVC") as! DialVC, animated: true)
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
            })
        }
    }
    func facebookViewControllerDoneWasPressed(sender: AnyObject) {
        print("donefb")
    }
    func facebookViewControllerCancelWasPressed(sender: AnyObject) {
        print("cancelfb")
    }
    
    func safariViewControllerDidFinish(controller: SFSafariViewController){
        print("done")
        controller.dismissViewControllerAnimated(true, completion: nil)
    }
    func safariViewController(controller: SFSafariViewController, didCompleteInitialLoad didLoadSuccessfully: Bool) {
        print("sf1")
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "segueLogin"{
            //let nextVC = segue.destinationViewController as! LoginVC
           // nextVC.name = name
            //nextVC.imgUrl = imgUrl
        }
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    
}
/*extension ViewController: FBSDKAppInviteDialogDelegate{
    func appInviteDialog(appInviteDialog: FBSDKAppInviteDialog!, didCompleteWithResults results: [NSObject : AnyObject]!) {
        //TODO
        // print(results)
    }
    
    func appInviteDialog(appInviteDialog: FBSDKAppInviteDialog!, didFailWithError error: NSError!) {
        //TODO
        let alertController = UIAlertController(title: "Network error", message: "Please check your internet connection...", preferredStyle: .Alert)
        // Create the actions
        let okAction = UIAlertAction(title: "Ok", style: UIAlertActionStyle.Default) {
            UIAlertAction in
        }
        alertController.addAction(okAction)
        self.presentViewController(alertController, animated: true, completion: nil)
    }
    
}*/
