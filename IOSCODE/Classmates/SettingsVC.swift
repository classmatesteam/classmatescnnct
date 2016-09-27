//
//  ProffessorNameVC.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
import FBSDKLoginKit
class SettingsVC: UIViewController, UIScrollViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    
    
    @IBOutlet weak var btnEdit: UIButton!
    
    @IBOutlet weak var scrolling: UIScrollView!
    @IBOutlet weak var viewRemoveClass: UIView!
    @IBOutlet weak var iconCamera: UIImageView!
    @IBOutlet weak var imgProfile: UIImageView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var viewSettings: UIView!
    @IBOutlet weak var btnRemoveClass: UIButton!
   
    var size: CGFloat = 40
    var arrClassName = [String]()
    var arrClassID = [String]()
    let btn: UIButton = UIButton()
    var imagePicker = UIImagePickerController()
    override func viewDidLoad() {
        super.viewDidLoad()
        getImage()
        configureDesign()
        setGestures()
    }
    func configureDesign(){
        userID = defaults.stringForKey("userID")!
        viewSettings.backgroundColor = colorLightBlue
       
        lblName.text = defaults.stringForKey("PROFILENAME")
        lblName.frame.origin.y = imgProfile.frame.origin.y + imgProfile.frame.size.height + 1
        
        if modelName == "iPhone 5" || modelName == "iPhone 5c" || modelName == "iPhone 5s" || modelName == "iPhone 4s"{
           size = 40
        }
        scrolling.hidden = true
        scrolling.frame.origin.y = btnRemoveClass.frame.origin.y
        scrolling.frame.origin.x = 0
        scrolling.frame.size.height = size
        scrolling.backgroundColor = UIColor.clearColor()
        scrolling.delegate = self

        scrolling.showsHorizontalScrollIndicator = false
        scrolling.showsVerticalScrollIndicator = false
        scrolling.scrollEnabled = true
        
        viewRemoveClass.frame.origin.y = 0
        viewRemoveClass.frame.size.height = size
        viewRemoveClass.backgroundColor = UIColor.clearColor()
        customButton(btn)
        
        imagePicker.delegate = self
        btnEdit.layer.borderWidth = 1.5
        btnEdit.layer.borderColor = colorLightBlue.CGColor
        btnEdit.layer.cornerRadius = 7.5
        btnEdit.hidden = true
        
        resizeToStretch(lblName)
        lblName.center.x = view.center.x
        
    }
    func getImage(){
        imgUrlString = defaults.stringForKey("PROFILE")!
        if imgUrlString.containsString("http"){
            ImageLoader.sharedLoader.imageForUrl(imgUrlString, completionHandler:{(image: UIImage?, url: String) in
               self.imgProfile.image = image
            })
            // imageURL = NSURL(string: imgUrlString)!
        }else{
            ImageLoader.sharedLoader.imageForUrl("\(imgApiLink)\(imgUrlString)", completionHandler:{(image: UIImage?, url: String) in
               self.imgProfile.image = image
            })
            //imageURL = NSURL(string: "http://brightdeveloper.net/classmates/assets/user/\(imgUrlString)")!
        }
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                if error == nil {
                    self.imgProfile.image = UIImage(data: data!)
                }
        })*/
        if modelName == "iPhone 5" || modelName == "iPhone 5c" || modelName == "iPhone 5s" || modelName == "iPhone 4s"{
            imgProfile.frame.size.width = 120
            imgProfile.frame.size.height = 120
            //imgProfile.contentMode = .Center
            imgProfile.frame = CGRectMake((viewSettings.frame.size.width / 2) - (imgProfile.frame.size.width / 2), (viewSettings.frame.size.height) - 90, 120, 120)
            
            iconCamera.frame.origin.x = imgProfile.frame.origin.x + imgProfile.frame.size.width - 20
        }
        imgProfile.layer.cornerRadius = imgProfile.frame.size.width/2.0
        imgProfile.clipsToBounds = true
        imgProfile.layer.borderWidth = 1.0
        imgProfile.layer.borderColor = UIColor.whiteColor().CGColor
    }
    func setGestures(){
        viewBack.userInteractionEnabled = true
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(SettingsVC.actionBack)))
        
        iconCamera.userInteractionEnabled = true
        imgProfile.userInteractionEnabled = true
        iconCamera.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(SettingsVC.actionImagePick)))
        imgProfile.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(SettingsVC.actionImagePick)))
    }
    func customButton(btn: UIButton){
        btn.titleLabel?.adjustsFontSizeToFitWidth = true
        btn.titleLabel?.textAlignment = .Center
        btn.frame.size.height = size
        btn.frame.size.width = size
        btn.addTarget(self, action: #selector(SettingsVC.buttonAction(_:)), forControlEvents: UIControlEvents.TouchUpInside)
        btn.backgroundColor = UIColor.whiteColor()
        btn.layer.cornerRadius = btn.frame.size.width/2.0
        btn.clipsToBounds = true
        btn.setTitleColor(colorLightBlue, forState: .Normal)
    }
    @IBAction func actionAddClass(sender: AnyObject) {
        self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("LoginVC") as! LoginVC, animated: true)
    }
    
    @IBAction func actionRemoveClass(sender: AnyObject) {
        removeClass()
    }
    
    func removeClass(){
        
        if arrClassName.count == 0{
            self.simpleAlert("Alert", message: "No Class Added.")
        }else{
            btnRemoveClass.hidden = true
            scrolling.hidden = false
            //scrolling.frame.size.width = ((btn.frame.size.width) + 10) * CGFloat(arrClassName.count) - 10
            viewRemoveClass.frame.size.width = ((btn.frame.size.width) + 10) * CGFloat(arrClassName.count) - 10
            print(scrolling.frame.size)
            print(viewRemoveClass.frame.size)
            for i in 0 ..< arrClassName.count{
                let btn1 = UIButton()
                btn1.titleLabel!.font =  UIFont(name: "Ubuntu", size: 10)
                btn1.setTitle(arrClassName[i], forState: UIControlState.Normal)
                btn1.tag = i
                customButton(btn1)
                //scrolling.center.x = self.view.center.x
                scrolling.contentSize = viewRemoveClass.bounds.size
                viewRemoveClass.center.x = self.view.center.x
                self.viewRemoveClass.addSubview(btn1)
                btn1.frame.origin.x = size
                btn1.frame.origin.x = CGFloat(i) * ((btn1.frame.origin.x) + 10)
               
            }
            if viewRemoveClass.frame.size.width >= view.frame.size.width {
                print("scroll")
               viewRemoveClass.frame.origin.x = 0
            }
        }

    }
    func buttonAction(sender: UIButton!) {
        print(arrClassID[sender.tag])
        let alert = UIAlertController(title: "Alert", message: "Are you sure you want to remove the selected class?", preferredStyle: .Alert)
        alert.addAction(UIAlertAction(title: "Yes", style: .Default){
            UIAlertAction in
            //remove
            /*let data = [
                "userid" : userID,
                "classid": self.arrClassID[sender.tag],
                "removeclass" : ""
            ]*/
            self.showHUD()
            Alamofire.request(.POST, apiLink, parameters:[
                "userid" : userID,
                "classid": self.arrClassID[sender.tag],
                "removeclass" : ""
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
                                self.arrClassName.removeAtIndex(sender.tag)
                                self.arrClassID.removeAtIndex(sender.tag)
                                self.scrolling.hidden = true
                                self.btnRemoveClass.hidden = false
                                self.viewRemoveClass.subviews.forEach({ $0.removeFromSuperview() }) // this gets things done
                                //self.removeClass()
                                defaults.setObject("true", forKey: "REMOVED")
                                self.hideHUD()
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

    @IBAction func actionInviteFriend(sender: AnyObject) {
        let text: String = "https://itunes.apple.com/us/app/classmates/id1133432658"
        let dataToShare: [AnyObject] = [text]
        let activityVC: UIActivityViewController = UIActivityViewController(activityItems: dataToShare, applicationActivities: nil)
        self.presentViewController(activityVC, animated: true, completion: nil)
    }
    @IBAction func actionLogout(sender: AnyObject) {
        let alert = UIAlertController(title: "Alert", message: "Logout now?", preferredStyle: .Alert)
        alert.addAction(UIAlertAction(title: "Yes", style: .Default){
            UIAlertAction in
            //logout now
           // FBSDKAccessToken.currentAccessToken()
            
                defaults.removeObjectForKey("userID")
                //FBSDKAccessToken.setCurrentAccessToken(nil)
                //FBSDKProfile.setCurrentProfile(nil)
                FBSDKLoginManager().logOut()
            self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("ViewController") as! ViewController, animated: true)
           
        })
        alert.addAction(UIAlertAction(title: "No", style: .Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    func actionImagePick(){
        self.view.endEditing(true)
        let  alert = UIAlertController(title: "Select Image", message: "",
                                       preferredStyle: UIAlertControllerStyle.ActionSheet)
        
        let galleryAction = UIAlertAction(title: "Gallery", style: UIAlertActionStyle.Default) {
            UIAlertAction in
            
            if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.PhotoLibrary){
                self.openGallery()
            }
        }
        let cameraAction = UIAlertAction(title: "Camera", style: UIAlertActionStyle.Default) {
            
            UIAlertAction in
            
            if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera){
                self.openCamera()
            }else {
                let alert = UIAlertView()
                alert.title = "Alert"
                alert.message = "Your Device Does Not Support Camera."
                alert.addButtonWithTitle("OK")
                alert.show()
            }
        }
        let cancelAction =  UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Default) {
            UIAlertAction in
        }
        alert.addAction(galleryAction)
        alert.addAction(cameraAction)
        alert.addAction(cancelAction)
        
        self.presentViewController(alert, animated: true, completion: nil)
    }
    func openCamera() {
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera){
            imagePicker.sourceType = UIImagePickerControllerSourceType.Camera
            self.presentViewController(imagePicker, animated: true, completion: nil)
        } else{
            openGallery()
        }
    }
    func openGallery() {
        imagePicker.sourceType = UIImagePickerControllerSourceType.PhotoLibrary
        if UIDevice.currentDevice().userInterfaceIdiom == .Phone{
            self.presentViewController(imagePicker, animated: true, completion: nil)
        }
    }
    func imagePickerController(picker: UIImagePickerController, didFinishPickingImage image: UIImage, editingInfo: [String : AnyObject]?) {
        imagePicker.allowsEditing = true
        self.imgProfile.image = image
        //editImgApi()
        btnEdit.hidden = false
        dismissViewControllerAnimated(true, completion: nil)
    }
  
    @IBAction func actionEditProfile(sender: AnyObject) {
        
      /*  let words: NSArray = lblName.text!.componentsSeparatedByCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
    
        var lname = ""
        for i in 1 ..< words.count {
            if i == 1{
                lname = "\(words[i])"
            }else{
                lname = "\(lname) \(words[i])"
            }
            
        }
        print(words[0])
        print(lname)
        print(userID)*/
         let data_ : NSData = UIImageJPEGRepresentation(imgProfile.image!, 0.5)!
        
        self.showHUD()
        Alamofire.upload(.POST, apiLink, multipartFormData: { multipartFormData in
            
                multipartFormData.appendBodyPart(
                    data: userID.dataUsingEncoding(NSUTF8StringEncoding)!,
                    name: "userid"
                )
            
               /* multipartFormData.appendBodyPart(
                    data: words[0].dataUsingEncoding(NSUTF8StringEncoding)!,
                    name: "fname"
                )
            
                multipartFormData.appendBodyPart(
                    data: lname.dataUsingEncoding(NSUTF8StringEncoding)!,
                    name: "lname"
                )*/
            
                multipartFormData.appendBodyPart(data: data_, name: "image",fileName: "file.jpeg",
                    mimeType: "image/jpeg")
                
                multipartFormData.appendBodyPart(
                    data: "".dataUsingEncoding(NSUTF8StringEncoding)!,
                    name: "profileedit"
                )
            
            },
            encodingCompletion: { encodingResult in
                switch encodingResult {
                case .Success(let upload, _, _):
                    upload.responseJSON { response in
                        
                        print("error")
                        debugPrint(response.result)
                        debugPrint(response.result.value)
                        //Array ( [name] => download.jpeg [type] => image/jpeg [tmp_name] => /tmp/phpyDUsSR [error] => 0 [size] => 6657 )
                        if String(response.result) == "SUCCESS"{
                            
                            if let JSON = response.result.value {
                                print("JSON: \(JSON)")
                                apiStatus = JSON["status"] as! String
                                if apiStatus == "success" {
                                    let message = JSON["message"] as! NSDictionary
                                    print(message)
                                    /*if let  fn = message["fname"] as? String {
                                        if let  ln = message["lname"] as? String {
                                            defaults.setObject("\(fn) \(ln)", forKey: "PROFILENAME")
                                        }
                                    }*/
                                    if let  userpic = message["userpic"] as? String {
                                        defaults.setObject(userpic, forKey: "PROFILE")
                                    }
                                    self.hideHUD()
                                    self.btnEdit.hidden = true
                                    self.simpleAlert("Alert", message: "Profile Saved Successfully.")
                                }else{
                                    self.hideHUD()
                                    self.simpleAlert("Alert", message: JSON["message"] as! String)
                                }
                            }
                        }else{
                            self.hideHUD()
                            self.simpleAlert("Network Error", message: "Please check your internet connection...")
                        }
                    }
                case .Failure(let encodingError):
                    
                    let alert = UIAlertView()
                    alert.title = "Alert"
                    alert.message = "Network Problem"
                    alert.addButtonWithTitle("OK")
                    alert.show()
                    
                }
        })

    }
    func resizeToStretch(label: UILabel) {
        let width: CGFloat = self.expectedWidth(label)
        var newFrame: CGRect = label.frame
        newFrame.size.width = width
        label.frame = newFrame
    }
    func expectedWidth(lbl: UILabel) -> CGFloat {
        lbl.numberOfLines = 1
        var unitSize = CGSizeZero
        if let textUnit = lbl.text {
            unitSize = textUnit.sizeWithAttributes([NSFontAttributeName:lbl.font])
        }
        let expectedLabelSize: CGSize = unitSize
        return expectedLabelSize.width
    }
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(true)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    override func prefersStatusBarHidden() -> Bool {
        return true
    }
    
}
/* func actionEditName(){
 let alert = UIAlertController(title: "Edit Name", message: "", preferredStyle: UIAlertControllerStyle.Alert)
 alert.addTextFieldWithConfigurationHandler ({ (textField) -> Void in
 textField.placeholder = "First Name"
 })
 alert.addTextFieldWithConfigurationHandler ({ (textField2) -> Void in
 textField2.placeholder = "Last Name"
 })
 alert.addAction(UIAlertAction(title: "Done", style: UIAlertActionStyle.Default, handler: { (action) -> Void in
 let textField = alert.textFields![0] as UITextField
 let textField2 = alert.textFields![1] as UITextField
 let fnameRange: String = self.range(textField)
 let lnameRange: String = self.range(textField2)
 if fnameRange.characters.count == 0 {
 self.simpleAlert("Alert", message: "Please Fill First Name")
 }else if lnameRange.characters.count == 0 {
 self.simpleAlert("Alert", message: "Please Fill Last Name")
 }else {
 let f:String = textField.text! as String
 let l:String = textField2.text! as String
 self.lblName.text = "\(f) \(l)"
 self.resizeToStretch(self.lblName)
 self.lblName.center.x = self.view.center.x
 // self.imgPencil.frame.origin.x = self.lblName.frame.origin.x + self.lblName.frame.size.width + 5
 self.btnEdit.hidden = false
 }
 }))
 alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Default, handler: { (action) -> Void in
 //nothing
 }))
 self.presentViewController(alert, animated: true, completion: nil)
 
 }*/
/*

 
 */