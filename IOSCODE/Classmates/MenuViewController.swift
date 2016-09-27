//
//  MenuViewController.swift
//  InteractiveSlideoutMenu
//
//  Created by Robert Chen on 2/7/16.
//
//  Copyright (c) 2016 Thorn Technologies LLC
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

import UIKit
import Alamofire
class MenuViewController : UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var imgMenuProfile: UIImageView!
    var interactor:Interactor? = nil
    
    var menuActionDelegate:MenuActionDelegate? = nil
    
    let menuItems = ["ADD CLASS", "SETTINGS", "INVITES"]
    let menuImages = ["addGClass", "settingsG", "invitationG"]
    //var userID = ""
    var count = 0
   
    override func viewDidLoad() {
        tableView.rowHeight = 90
        configureDesign()
    }
    func configureDesign(){
        
        userID = defaults.stringForKey("userID")!
        if modelName == "iPhone 5c" || modelName == "iPhone 5s" || modelName == "iPhone 5" || modelName == "iPhone 4" || modelName == "iPhone 4s"{
           imgMenuProfile.frame.origin.x = 8
        }
        imgUrlString = defaults.stringForKey("PROFILE")!
        
        if imgUrlString.containsString("http"){
            ImageLoader.sharedLoader.imageForUrl(imgUrlString, completionHandler:{(image: UIImage?, url: String) in
               self.imgMenuProfile.image = image
            })
            // imageURL = NSURL(string: imgUrlString)!
        }else{
            ImageLoader.sharedLoader.imageForUrl("\(imgApiLink)\(imgUrlString)", completionHandler:{(image: UIImage?, url: String) in
                self.imgMenuProfile.image = image
            })
            //imageURL = NSURL(string: "http://brightdeveloper.net/classmates/assets/user/\(imgUrlString)")!
        }
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                if error == nil {
                    self.imgMenuProfile.image = UIImage(data: data!)
                }
        })*/
        
        imgMenuProfile.layer.cornerRadius = imgMenuProfile.frame.size.width/2.0
        imgMenuProfile.clipsToBounds = true
        let gradientLayer = CAGradientLayer()
        gradientLayer.frame = (view.layer.bounds)
        gradientLayer.colors = [UIColor.whiteColor(),colorLightBlue].map{$0.CGColor}
        gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
        
        // Render the gradient to UIImage
        UIGraphicsBeginImageContext(gradientLayer.bounds.size)
        gradientLayer.renderInContext(UIGraphicsGetCurrentContext()!)
        //let gradientImage = UIGraphicsGetImageFromCurrentImageContext()
        //UIGraphicsEndImageContext()
        view.layer.insertSublayer(gradientLayer, atIndex: 0)
        hitInviteCountApi()
    }
    @IBAction func handleGesture(sender: UIPanGestureRecognizer) {
        let translation = sender.translationInView(view)

        let progress = MenuHelper.calculateProgress(translation, viewBounds: view.bounds, direction: .Left)
        
        MenuHelper.mapGestureStateToInteractor(
            sender.state,
            progress: progress,
            interactor: interactor){
                self.dismissViewControllerAnimated(true, completion: nil)
        }
    }
    
    @IBAction func closeMenu(sender: AnyObject) {
        dismissViewControllerAnimated(true, completion: nil)
    }
    func hitInviteCountApi(){
        /*let data = [
            "userid" : userID,
            "invitecount" : ""
        ]*/
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
                            self.count = message.count
                            //end for loop
                           
                           self.tableView.reloadData()
                        }
                    }
                }
        }//end api
    }
    override func prefersStatusBarHidden() -> Bool {
        return true
    }
}

extension MenuViewController : UITableViewDataSource {
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menuItems.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("cell")! as! MenuTableViewCell
        cell.lblCell.text = menuItems[indexPath.row]
        cell.imgCell.image = UIImage(named: menuImages[indexPath.row])
        
        if indexPath.row == 2 {
             print("count1")
            if count == 0{
                print("c1")
                cell.itemCount.hidden = true
                }else{
                 print("c2")
                cell.itemCount.hidden = false
                cell.itemCount.text = String(count)
            }
        }else{
            print("count2")
            cell.itemCount.hidden = true
        }
        return cell
    }
    func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.01
    }
}

extension MenuViewController : UITableViewDelegate {
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        tableView.deselectRowAtIndexPath(indexPath, animated: true)
        
        let selectedCell:MenuTableViewCell = tableView.cellForRowAtIndexPath(indexPath)! as! MenuTableViewCell
        initAppearNavigation(selectedCell.contentView)
        selectedCell.lblCell.textColor = UIColor.whiteColor()
        selectedCell.imgCell.tintColor = UIColor.whiteColor()
        selectedCell.imgCell.image = selectedCell.imgCell.image!.imageWithRenderingMode(UIImageRenderingMode.AlwaysTemplate)
        switch indexPath.row {
        case 0:
            menuActionDelegate?.openSegue("push", sender: nil)
        case 1:
            menuActionDelegate?.openSegue("segueSettings", sender: nil)
        case 2:
            menuActionDelegate?.openSegue("segueAcceptInvite", sender: nil)
        default:
            break
        }
    }
}