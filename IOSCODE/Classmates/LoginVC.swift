//
//  LoginVC.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
class LoginVC: UIViewController{
    
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var btnLoginManually: UIButton!
    @IBOutlet weak var btnLoginUniversity: UIButton!
    
    @IBOutlet weak var imgProfile: UIImageView!
    @IBOutlet weak var lblName: UILabel!
    
    
    var name = ""
    //var imgUrl = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureDesign()
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
       // self.navigationController?.navigationBarHidden = true
        
    }
   
    func configureDesign(){
        btnLoginUniversity.tintColor = colorLightBlue
        btnLoginUniversity.layer.cornerRadius = UIScreen.mainScreen().bounds.height / 22.0
        btnLoginUniversity.clipsToBounds = true
        btnLoginManually.layer.cornerRadius = UIScreen.mainScreen().bounds.height / 22.0
        btnLoginManually.clipsToBounds = true
        
        initAppearance(btnLoginManually)
        initAppearance1()
        //initAppearNavigation(self.navigationController!)
        if modelName == "iPhone 5" || modelName == "iPhone 5c" || modelName == "iPhone 5s"{
            imgProfile.frame.size.height = 100
            imgProfile.frame.size.width = 100
        }
        viewBack.userInteractionEnabled = true
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(LoginVC.actionBack)))
        lblName.frame.origin.y = imgProfile.frame.origin.y + imgProfile.frame.size.height
        imgProfile.layer.cornerRadius = imgProfile.frame.size.width/2.0
        imgProfile.clipsToBounds = true
        
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
       /* 
        imgUrl = defaults.stringForKey("PROFILE")!
        let imgURL: NSURL = NSURL(string: imgUrl)!
        let request: NSURLRequest = NSURLRequest(URL: imgURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
            if error == nil {
                self.imgProfile.image = UIImage(data: data!)
            }
        })*/
        imgProfile.layer.borderWidth = 1.0
        imgProfile.layer.borderColor = UIColor.whiteColor().CGColor
        name = defaults.stringForKey("PROFILENAME")!
        lblName.text = "Hello \(name) !"
        lblName.textColor = UIColor.whiteColor()
 
    }
    func initAppearance1() -> Void {
        let gradientLayer = CAGradientLayer()
        gradientLayer.frame = (self.btnLoginUniversity?.bounds)!
        gradientLayer.colors = [UIColor.groupTableViewBackgroundColor(),UIColor.whiteColor(),UIColor.groupTableViewBackgroundColor()].map{$0.CGColor}
        gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
        
        UIGraphicsBeginImageContext(gradientLayer.bounds.size)
        gradientLayer.renderInContext(UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        self.btnLoginUniversity.setBackgroundImage(image, forState: .Normal)
    }
    @IBAction func actionManually(sender: AnyObject) {
        self.performSegueWithIdentifier("segueManually", sender: self)
        //self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("ClassNumberVC") as! ClassNumberVC, animated: true)
    }
    
    
    @IBAction func actionUniversity(sender: AnyObject) {
    }
    
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(true)
        //self.navigationController?.navigationBarHidden = false
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    override func prefersStatusBarHidden() -> Bool {
        return true
    }
}