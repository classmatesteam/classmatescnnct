//
//  ConfigFile.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
let colorLightBlue = UIColor(red: 0x24/255, green: 0x8a/255, blue: 0x81/255, alpha: 1.0)
let colorDarkBlue = UIColor(red: 0x00/255, green: 0x4a/255, blue: 0x5e/255, alpha: 1.0)
let colorPink = UIColor(red: 0.0/255.0, green: 202.0/255.0, blue: 215.0/255.0, alpha: 1.0)
//let darkBlue = UIColor(red: 0.0/255.0, green: 185.0/255.0, blue: 197.0/255.0, alpha: 1.0)
let colorTop = UIColor(red: 0x1d/255, green: 0x84/255, blue: 0xa0/255, alpha: 1.0)
let colorBottom = UIColor(red: 0x0e/255, green: 0x56/255, blue: 0x74/255, alpha: 1.0)

var counter = ""

var myMutableString = NSMutableAttributedString()
let modelName = UIDevice.currentDevice().modelName

let defaults = NSUserDefaults.standardUserDefaults()
var userID = ""
var hudView = UIView()
var animImage = UIImageView(frame: CGRectMake(0, 0, 80, 80))
let apiLink = "http://classmatescnnct.com/index.php/Webservices"
let imgApiLink = "http://classmatescnnct.com/assets/user/"
var apiStatus = ""
var imgUrlString = ""
var imageURL: NSURL!
var imageCache = [String:UIImage]()
var chatNext = false
var shouldShow = true;
//sort(11)
var arrDefaultEventID = [String]()
var arrDefaultCourseName = [String]()
var arrDefaultTopicName = [String]()
var arrDefaultStartTime = [String]()
var arrDefaultLocation = [String]()
var arrDefaultCreatedDate = [String]()
var arrDefaultParticipants = [Int]()
var arrDefaultEventCUserName = [String]()
var arrDefaultCUserPic = [String]()
var arrDefaultStatus = [String]()
var arrDefaultEventCUserID = [String]()

//sorted array(11)
var arrSortEventID = [String]()
var arrSortCourseName = [String]()
var arrSortTopicName = [String]()
var arrSortStartTime = [String]()
var arrSortLocation = [String]()
var arrSortCreatedDate = [String]()
var arrSortUserName = [String]()
var arrSortPic = [String]()
var arrSortParticipants = [Int]()
var arrSortEventCUserID = [String]()
var arrSortStatus = [String]()

func initAppearance(sender: UIButton){
    let gradientLayer = CAGradientLayer()
    gradientLayer.frame = (sender.bounds)
    gradientLayer.colors = [colorTop,colorBottom].map{$0.CGColor}
    //gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
    //gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
    gradientLayer.startPoint = CGPoint(x: 0.5, y: 0.0)
    gradientLayer.endPoint = CGPoint(x: 0.5, y: 1.0)
    UIGraphicsBeginImageContext(gradientLayer.bounds.size)
    gradientLayer.renderInContext(UIGraphicsGetCurrentContext()!)
    let gradientImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    sender.setBackgroundImage(gradientImage, forState: .Normal)
}
func filterTextField(textField: UITextField){
    
    textField.textAlignment = NSTextAlignment.Center
    textField.textColor = colorLightBlue
    //textField.borderStyle = UITextBorderStyle.Line
    //textField.layer.borderWidth = 1.5
    //textField.layer.borderColor = colorLightBlue.CGColor
    textField.backgroundColor = UIColor.whiteColor()
    textField.layer.cornerRadius = 10.0
    textField.tintColor = colorLightBlue
    textField.font = UIFont(name: "Ubuntu", size: 14.0)
}
func filterButton(cancelButton: UIButton){
    cancelButton.setTitle("Cancel", forState: .Normal)
    cancelButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
    cancelButton.titleLabel?.font = UIFont(name: "Ubuntu", size: 14.0)!
}

func initAppearNavigation(view: UIView){
    let gradientLayer = CAGradientLayer()
    gradientLayer.frame = (view.layer.bounds)
    gradientLayer.colors = [colorBottom,colorTop].map{$0.CGColor}
    gradientLayer.startPoint = CGPoint(x: 0.5, y: 0.0)
    gradientLayer.endPoint = CGPoint(x: 0.5, y: 1.0)
    
    // Render the gradient to UIImage
    UIGraphicsBeginImageContext(gradientLayer.bounds.size)
    gradientLayer.renderInContext(UIGraphicsGetCurrentContext()!)
    let gradientImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    view.layer.backgroundColor = UIColor(patternImage: gradientImage).CGColor
    //view.layer.insertSublayer(gradientLayer, atIndex: 0)
    //view.layer.addSublayer(gradientLayer)
    
    //navigationController.setBackgroundImage(gradientImage, forBarMetrics: UIBarMetrics.Default)
    //navigationController.navigationBar.tintColor = UIColor.grayColor()
    //navigationController.navigationBar.titleTextAttributes = [ NSFontAttributeName: UIFont(name: "Ubuntu", size: 20)!]
}
func initNavigationItem(button: UIButton, title: String, imgName: String){
    button.setImage(UIImage(named: imgName), forState: .Normal)
    button.frame = CGRectMake(0, 0, 53, 31)
    //button.imageView?.frame.size = U
    let label: UILabel = UILabel(frame: CGRectMake(1, 5, 50, 20))
    label.font = UIFont(name: "Ubuntu", size: 10)
    label.text = title
    label.textAlignment = .Center
    label.textColor = UIColor.darkGrayColor()
    label.backgroundColor = UIColor.clearColor()
    label.frame.origin.y =  (button.imageView?.frame.origin.y)! + 21
    
    button.addSubview(label)

}
extension UIViewController {
    
   
    func range(textField: UITextField) -> String{
        return textField.text!.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
    }
    func simpleAlert(title: String, message: String){
        let alertError = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.Alert)
        alertError.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
        
        self.presentViewController(alertError, animated: true, completion: nil)
    }
    func alertToMsg(string: String){
        let alert = UIAlertController(title: "Alert", message: string, preferredStyle: .Alert)
        alert.addAction(UIAlertAction(title: "OK", style: .Default){
            UIAlertAction in
            self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("GroupMessagesVC") as! GroupMessagesVC, animated: true)
            
            })
        
        self.presentViewController(alert, animated: true, completion: nil)
    }
    func actionPList(eventId:String){
        let nextVC = self.storyboard?.instantiateViewControllerWithIdentifier("ParticipantsListVC") as! ParticipantsListVC
        self.navigationController?.pushViewController(nextVC, animated: true)
        nextVC.eventID = eventId
    }
    func actionBack(){
        self.navigationController?.popViewControllerAnimated(true)
    }
    func showHUD() {
        hudView.frame = CGRectMake(0, 0, view.frame.size.width, view.frame.size.height)
        hudView.backgroundColor = UIColor.whiteColor()
        hudView.alpha = 0.7
        
        let imagesArr = ["h0", "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "h9"]
        var images : [UIImage] = []
        for i in 0 ..< imagesArr.count {
            images.append(UIImage(named: imagesArr[i])!)
        }
        animImage.animationImages = images
        animImage.animationDuration = 0.7
        animImage.center = hudView.center
        hudView.addSubview(animImage)
        animImage.startAnimating()
        
        view.addSubview(hudView)
    }
    
    func hideHUD() {  hudView.removeFromSuperview()  }
}

public extension UIDevice {
    
    var modelName: String {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8 where value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value)))
        }
        
        switch identifier {
        case "iPod5,1":                                 return "iPod Touch 5"
        case "iPod7,1":                                 return "iPod Touch 6"
        case "iPhone3,1", "iPhone3,2", "iPhone3,3":     return "iPhone 4"
        case "iPhone4,1":                               return "iPhone 4s"
        case "iPhone5,1", "iPhone5,2":                  return "iPhone 5"
        case "iPhone5,3", "iPhone5,4":                  return "iPhone 5c"
        case "iPhone6,1", "iPhone6,2":                  return "iPhone 5s"
        case "iPhone7,2":                               return "iPhone 6"
        case "iPhone7,1":                               return "iPhone 6 Plus"
        case "iPhone8,1":                               return "iPhone 6s"
        case "iPhone8,2":                               return "iPhone 6s Plus"
        case "iPhone8,4":                               return "iPhone SE"
        case "iPad2,1", "iPad2,2", "iPad2,3", "iPad2,4":return "iPad 2"
        case "iPad3,1", "iPad3,2", "iPad3,3":           return "iPad 3"
        case "iPad3,4", "iPad3,5", "iPad3,6":           return "iPad 4"
        case "iPad4,1", "iPad4,2", "iPad4,3":           return "iPad Air"
        case "iPad5,3", "iPad5,4":                      return "iPad Air 2"
        case "iPad2,5", "iPad2,6", "iPad2,7":           return "iPad Mini"
        case "iPad4,4", "iPad4,5", "iPad4,6":           return "iPad Mini 2"
        case "iPad4,7", "iPad4,8", "iPad4,9":           return "iPad Mini 3"
        case "iPad5,1", "iPad5,2":                      return "iPad Mini 4"
        case "iPad6,3", "iPad6,4", "iPad6,7", "iPad6,8":return "iPad Pro"
        case "AppleTV5,3":                              return "Apple TV"
        case "i386", "x86_64":                          return "Simulator"
        default:                                        return identifier
        }
    }
    
}