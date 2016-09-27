//
//  ViewController.swift
//  CircularSliderDemo
//
//  Created by Mallu on 6/14/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import UIKit
import Alamofire
protocol MenuActionDelegate {
    func openSegue(segueName: String, sender: AnyObject?)
}
class DialVC: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UIGestureRecognizerDelegate, UITextFieldDelegate {
    
    var imgView = UIImageView()
    let interactor = Interactor()
    
    
    @IBOutlet weak var viewMenu: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewAdd: UIView!
    @IBOutlet weak var viewEvent: UIView!
    @IBOutlet weak var viewMessages: UIView!
    @IBOutlet weak var viewTopic: UIView!
    
    @IBOutlet weak var viewLine: UIView!
    @IBOutlet weak var collectionView: UICollectionView!

    var cellId: String = "cellId"
    var cellId2: String = "cellId2"
    
    var onceOnly = false
    //var userID = ""
    var arrClassID = [String]()
    var arrClassName = [String]()
    var arrProffessorName = [String]()
    
    var thumbnailCache = [NSObject : AnyObject]()
    var showingSettings: Bool!
    var settingsView: UIView!
    var radiusLabel: UILabel!
    var radiusSlider: UISlider!
    var angularSpacingLabel: UILabel!
    var angularSpacingSlider: UISlider!
    var xOffsetLabel: UILabel!
    var xOffsetSlider: UISlider!
    var exampleSwitch: UISegmentedControl!
    var dialLayout: AWCollectionViewDialLayout!
    var cell_height: CGFloat!
    var type: Int!
    var items = NSArray()
    var imgUrl = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        userID = defaults.stringForKey("userID")!
        self.imgView.image = UIImage(named: "prf")
       configureDesign()
        
        type = 0
        showingSettings = false
        collectionView.registerNib(UINib(nibName: "dialCell", bundle: NSBundle.mainBundle()), forCellWithReuseIdentifier: cellId)
        collectionView.registerNib(UINib(nibName: "dialCell2", bundle: NSBundle.mainBundle()), forCellWithReuseIdentifier: cellId2)
        
        //var error: NSError
        //var jsonPath: String = NSBundle.mainBundle().pathForResource("photos", ofType: "json")!
        //var jsonString: String = String(contentsOfFile: jsonPath, encoding: NSUTF8StringEncoding, error: nil)
       // NSLog("jsonString:%@", jsonString)
       // items = NSJSONSerialization.JSONObjectWithData(jsonString.dataUsingEncoding(NSUTF8StringEncoding), options: kNilOptions, error: error)
        
        settingsView = UIView(frame: CGRectMake(0, self.view.frame.size.height, self.view.frame.size.width, self.view.frame.size.height - 44))
        settingsView.backgroundColor = UIColor(white: 0.0, alpha: 0.4)
        self.view!.addSubview(settingsView)
        self.buildSettings()

        let radius: CGFloat = CGFloat(radiusSlider.value * 1000)
        let angularSpacing: CGFloat = CGFloat(angularSpacingSlider.value * 90)
        let xOffset: CGFloat = CGFloat(xOffsetSlider.value * 320)
        let cell_width: CGFloat = 240
        cell_height = 100
        radiusLabel.text = "Radius: \(Int(radius))"
        angularSpacingLabel.text = "Angular spacing: \(Int(angularSpacing))"
        xOffsetLabel.text = "X offset: \(Int(xOffset))"
        dialLayout = AWCollectionViewDialLayout(radius: radius, andAngularSpacing: angularSpacing, andCellSize: CGSizeMake(cell_width, cell_height), andItemHeight: cell_height, andXOffset: xOffset)
        //dialLayout = AWCollectionViewDialLayout(radius: radius, andAngularSpacing: angularSpacing, andCellSize: CGSizeMake(cell_width, cell_height), andAlignment: .WHEELALIGNMENTCENTER, andItemHeight: cell_height, andXOffset: xOffset)
        
        dialLayout.setShouldSnap(true)
        collectionView.collectionViewLayout = dialLayout
        dialLayout.scrollDirection = .Horizontal
        //editBtn.target = self
        //editBtn.action = "toggleSettingsView"
        self.switchExample()
        calTimeZone()
    }
    func buildSettings(){
        var viewArr: [AnyObject] = NSBundle.mainBundle().loadNibNamed("iphone_settings_view", owner: self, options: nil)
        let innerView: UIView = viewArr[0] as! UIView
        var frame: CGRect = self.view.bounds
        frame.origin.y = (self.view.frame.size.height / 2 - frame.size.height / 2) / 2
        innerView.frame = frame
        settingsView.addSubview(innerView)
        innerView.setNeedsLayout()
        innerView.layoutIfNeeded()
        
        radiusLabel = (innerView.viewWithTag(100) as! UILabel)
        radiusSlider = (innerView.viewWithTag(200) as! UISlider)
        radiusSlider.addTarget(self, action: #selector(DialVC.updateDialSettings), forControlEvents: .ValueChanged)
        angularSpacingLabel = (innerView.viewWithTag(101) as! UILabel)
        angularSpacingSlider = (innerView.viewWithTag(201) as! UISlider)
        angularSpacingSlider.addTarget(self, action: #selector(DialVC.updateDialSettings), forControlEvents: .ValueChanged)
        
        xOffsetLabel = (innerView.viewWithTag(102) as! UILabel)
        xOffsetSlider = (innerView.viewWithTag(202) as! UISlider)
        xOffsetSlider.addTarget(self, action: #selector(DialVC.updateDialSettings), forControlEvents: .ValueChanged)
        exampleSwitch = (innerView.viewWithTag(203) as! UISegmentedControl)
        exampleSwitch.addTarget(self, action: #selector(DialVC.switchExample), forControlEvents: .ValueChanged)
    }
    
    func switchExample(){
        type = Int(exampleSwitch.selectedSegmentIndex)
        var radius: CGFloat = 0
        var angularSpacing: CGFloat = 0
        var xOffset: CGFloat = 0
       
       if modelName == "iPhone 5" || modelName == "iPhone 5c" || modelName == "iPhone 5s" || modelName == "iPhone 4s"{
            dialLayout.cellSize = CGSizeMake(340, 100)
            //dialLayout.wheelType = WHEELALIGNMENTLEFT
            dialLayout.setShouldFlip(false)
            radius = 170
            angularSpacing = 35
            xOffset = 170
        }else{
            dialLayout.cellSize = CGSizeMake(340, 100)
            //dialLayout.wheelType = WHEELALIGNMENTLEFT
            dialLayout.setShouldFlip(false)
            radius = 250
            angularSpacing = 25
            xOffset = 220
        }
        /*if type == 0 {
            dialLayout.cellSize = CGSizeMake(340, 100)
            //dialLayout.wheelType = WHEELALIGNMENTLEFT
            dialLayout.setShouldFlip(false)
            radius = 300
            angularSpacing = 25
            xOffset = 170
        }
        else if type == 1 {
            dialLayout.cellSize = CGSizeMake(260, 50)
            //dialLayout.wheelType = WHEELALIGNMENTCENTER
            dialLayout.setShouldFlip(true)
            radius = 320
            angularSpacing = 5
            xOffset = 124
        }*/
        //var angularSpacing: CGFloat = 0
        //var xOffset: CGFloat = 0
        radiusLabel.text = "Radius: \(Int(radius))"
        radiusSlider.value = Float(radius / 1000)
        dialLayout.dialRadius = radius
        angularSpacingLabel.text = "Angular spacing: \(Int(angularSpacing))"
        angularSpacingSlider.value = Float(angularSpacing / 90)
        dialLayout.AngularSpacing = angularSpacing
        xOffsetLabel.text = "X offset: \(Int(xOffset))"
        xOffsetSlider.value = Float(xOffset / 320)
        dialLayout.xOffset = xOffset
        collectionView.reloadData()
    }
    func updateDialSettings(){
        type = Int(exampleSwitch.selectedSegmentIndex)
        //var radius: CGFloat = 0
        //var angularSpacing: CGFloat = 0
       // var xOffset: CGFloat = 0
        let radius: CGFloat = CGFloat(radiusSlider.value * 1000)
        let angularSpacing: CGFloat = CGFloat(angularSpacingSlider.value * 90)
        let xOffset: CGFloat = CGFloat(xOffsetSlider.value * 320)
        radiusLabel.text = "Radius: \(Int(radius))"
        dialLayout.dialRadius = radius
        angularSpacingLabel.text = "Angular spacing: \(Int(angularSpacing))"
        dialLayout.AngularSpacing = angularSpacing
        xOffsetLabel.text = "X offset: \(Int(xOffset))"
        dialLayout.xOffset = xOffset
        dialLayout.invalidateLayout()
        //[collectionView reloadData];
        NSLog("updateDialSettings")
    }
    /*func toggleSettingsView() {
        var frame: CGRect = settingsView.frame
        if (showingSettings != nil) {
            //editBtn.title = "Edit"
            frame.origin.y = self.view.frame.size.height
        }
        else {
            //editBtn.title = "Close"
            frame.origin.y = 44
        }
        UIView.animateWithDuration(0.3, delay: 0, options: .CurveEaseOut, animations: {() -> Void in
            self.settingsView.frame = frame
            }, completion: {(finished: Bool) -> Void in
        })
        showingSettings = !showingSettings
    }*/
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return arrClassID.count
    }
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 1
    }
    func collectionView(cv: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        var cell: UICollectionViewCell
      
        cell = cv.dequeueReusableCellWithReuseIdentifier(cellId, forIndexPath: indexPath)
        
        //var item: [NSObject : AnyObject] = self.items[indexPath.item] as! [NSObject : AnyObject]
        //var playerName: String = "MAT232"
        let nameLabel: UILabel = (cell.viewWithTag(101) as! UILabel)
        nameLabel.text = arrClassName[indexPath.row]
    
        let view: UIView = (cell.viewWithTag(102))!
        view.backgroundColor = UIColor.whiteColor()
           
        let imgView: UIImageView = (cell.viewWithTag(100) as! UIImageView)
        imgView.image = UIImage(named: "classDot")
        imgView.hidden = true
        
        let imgView2: UIImageView = (cell.viewWithTag(111) as! UIImageView)
        imgView2.image = UIImage(named: "classBoardG")
        return cell
    }
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        //clearColor()
        let cell: UICollectionViewCell = collectionView.cellForItemAtIndexPath(indexPath)!
        //clearColor(cell)
        let nameLabel: UILabel = (cell.viewWithTag(101) as! UILabel)
        nameLabel.textColor = colorLightBlue
        let imgView2: UIImageView = (cell.viewWithTag(111) as! UIImageView)
        imgView2.tintColor = colorLightBlue
        imgView2.image = imgView2.image!.imageWithRenderingMode(UIImageRenderingMode.AlwaysTemplate)
       print(arrClassName[indexPath.row])
        let nextVC = self.storyboard?.instantiateViewControllerWithIdentifier("AllEventsVC") as! AllEventsVC
        nextVC.courseName = arrClassName[indexPath.row]
        self.navigationController?.pushViewController(nextVC, animated: true)
        
        let seconds = 1.5
        let delay = seconds * Double(NSEC_PER_SEC)  // nanoseconds per seconds
        let dispatchTime = dispatch_time(DISPATCH_TIME_NOW, Int64(delay))
        dispatch_after(dispatchTime, dispatch_get_main_queue(), {
            
            // here code perfomed with delay
            nameLabel.textColor = UIColor.darkGrayColor()
            imgView2.tintColor = UIColor.darkGrayColor()
        })
       
    }
    func collectionView(collectionView: UICollectionView, didDeselectItemAtIndexPath indexPath: NSIndexPath) {
        let cell: UICollectionViewCell = collectionView.cellForItemAtIndexPath(indexPath)!
        let nameLabel: UILabel = (cell.viewWithTag(101) as! UILabel)
        nameLabel.textColor = UIColor.darkGrayColor()
        let imgView2: UIImageView = (cell.viewWithTag(111) as! UIImageView)
        imgView2.tintColor = UIColor.darkGrayColor()
    }
   /* func collectionView(collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAtIndex section: Int) -> UIEdgeInsets {
        return UIEdgeInsetsMake(500, 0, 0, 0)  // top, left, bottom, right
    }*/
    func collectionView(collectionView: UICollectionView, willDisplayCell cell: UICollectionViewCell, forItemAtIndexPath indexPath: NSIndexPath) {
        let initialPinchPoint = CGPointMake(self.collectionView.center.x + self.collectionView.contentOffset.x, self.collectionView.center.y + self.collectionView.contentOffset.y)
        if let indexPath = self.collectionView.indexPathForItemAtPoint(initialPinchPoint){
            print(indexPath.row)
        }
        print(initialPinchPoint)
        if !onceOnly {
            
            //self.collectionView.scrollToItemAtIndexPath(indexPath, atScrollPosition: .CenteredVertically, animated: true)
            if arrClassID.count > 0 {
            //let indexToScrollTo = NSIndexPath(forRow: 0, inSection: 0)
            //self.collectionView.scrollToItemAtIndexPath(indexToScrollTo, atScrollPosition: .CenteredVertically, animated: false)
            
             //onceOnly = true
            }
        }
    }
    //Depending on the screen size you need another inset to make it look centered. Then you have to calculate it manually, but it should not be that hard. You know the available space, the number of items and the cell size. Just calculate the section inset from top.
    /*func intFromHexString(hexStr: String) -> UInt {
        var hexInt: UInt = 0
        // Create scanner
        var scanner: NSScanner = NSScanner.scannerWithString(hexStr)
        // Tell scanner to skip the # character
        scanner.charactersToBeSkipped = NSCharacterSet(charactersInString: "#")
        // Scan hex value
        scanner.scanHexInt(hexInt)
        return hexInt
    }
    func colorFromHex(hexString: String) -> UIColor {
        let hexint: UInt = self.intFromHexString(hexString)
        // Create color object, specifying alpha as well
        let color: UIColor = UIColor(red: ((((hexint & 0xFF0000) >> 16) as! CGFloat)) / 255, green: ((((hexint & 0xFF00) >> 8) as! CGFloat)) / 255, blue: (((hexint & 0xFF) as! CGFloat)) / 255, alpha: 1)
        return color
    }*/
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
        if defaults.stringForKey("REMOVED") != nil{
            hitClassListApi()
            defaults.removeObjectForKey("REMOVED")
        }
        collectionView.reloadData()
        imgUrlString = defaults.stringForKey("PROFILE")!
        
        if imgUrlString.containsString("http"){
            ImageLoader.sharedLoader.imageForUrl(imgUrlString, completionHandler:{(image: UIImage?, url: String) in
                self.imgView.image = image
            })
        }else{
            ImageLoader.sharedLoader.imageForUrl("\(imgApiLink)\(imgUrlString)", completionHandler:{(image: UIImage?, url: String) in
                self.imgView.image = image
            })

        }
        /*let request: NSURLRequest = NSURLRequest(URL: imageURL)
        NSURLConnection.sendAsynchronousRequest(
            request, queue: NSOperationQueue.mainQueue(),
            completionHandler: {(response: NSURLResponse?,data: NSData?,error: NSError?) -> Void in
                if error == nil {
                    self.imgView.image = UIImage(data: data!)
                }
        })*/
        //navigationController?.navigationItem.hidesBackButton = true
    }
    func calTimeZone(){
        let timeFormatter = NSDateFormatter()
        print(String(timeFormatter.timeZone))
        let zone = String(timeFormatter.timeZone)
        var z = ""
        //let iOStimeZones = NSTimeZone.knownTimeZoneNames()
        for index in zone.startIndex..<zone.endIndex {
            if "\(zone[index])" == " " {
                return
            }else{
                z = "\(z)\(zone[index])"
                defaults.setObject(z, forKey: "timeZone")
            }
        }
    }
    func configureDesign(){
        
        viewLine.frame.origin.y = viewTop.frame.origin.y + viewTop.frame.size.height
        collectionView.frame.origin.y = (self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height)
        collectionView.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewLine.frame.origin.y) + (self.viewLine.frame.size.height))
        viewTop.backgroundColor = UIColor.clearColor()
        
        //initAppearNavigation(viewTop)
        viewMenu.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(DialVC.actionMenu)))
        viewAdd.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(DialVC.actionAddEvent)))
        viewEvent.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(DialVC.actionMyEvent)))
        viewMessages.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(DialVC.actionMessages)))
        viewTopic.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(DialVC.actionHotTopics)))
    
        let imgSize = self.view.bounds.size.height/5.0
        imgView.frame = CGRect(x: 0, y: ((UIScreen.mainScreen().bounds.midY) - ((imgSize-20)/2.5)), width: imgSize, height: imgSize)
        self.view.addSubview(imgView)
      
        imgView.layer.cornerRadius = imgView.frame.size.width/2.0
        imgView.clipsToBounds = true
        imgView.userInteractionEnabled = true
        imgView.contentMode = .ScaleAspectFill
        imgView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(DialVC.actionImage)))
        
        // attach long press gesture to collectionView
        let lpgr: UILongPressGestureRecognizer = UILongPressGestureRecognizer(target: self, action: #selector(DialVC.handleLongPress(_:)))
        lpgr.delegate = self
        lpgr.delaysTouchesBegan = true
        self.collectionView.addGestureRecognizer(lpgr)
        hitClassListApi()
        
    }
    func handleLongPress(gestureRecognizer: UILongPressGestureRecognizer) {
       
        if gestureRecognizer.state != .Ended {
            return
        }
        let p: CGPoint = gestureRecognizer.locationInView(self.collectionView)
        if let indexPath = self.collectionView.indexPathForItemAtPoint(p){
            print(arrClassName[indexPath.row])
            
            //let cell: UICollectionViewCell = collectionView.cellForItemAtIndexPath(indexPath)!
            //clearColor(collectionView)
            let cell: UICollectionViewCell = self.collectionView.cellForItemAtIndexPath(indexPath)!
            let nameLabel: UILabel = (cell.viewWithTag(101) as! UILabel)
            let imgView2: UIImageView = (cell.viewWithTag(111) as! UIImageView)
            nameLabel.textColor = colorLightBlue
            imgView2.tintColor = colorLightBlue
            imgView2.image = imgView2.image!.imageWithRenderingMode(UIImageRenderingMode.AlwaysTemplate)
            let alert = UIAlertController(title: "Reset Class Name", message: "", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addTextFieldWithConfigurationHandler ({ (textField) -> Void in
                textField.placeholder = "Enter Class Name"
                textField.text = self.arrClassName[indexPath.row]
                textField.delegate = self
                textField.autocapitalizationType = .AllCharacters
            })
            alert.addAction(UIAlertAction(title: "Reset", style: UIAlertActionStyle.Default, handler: { (action) -> Void in
                let textField = alert.textFields![0] as UITextField
                let nameRange: String = self.range(textField)
                if nameRange.characters.count == 0{
                    self.simpleAlert("Alert", message: "Please Enter Class Name.")
                }else if nameRange.characters.count != 6{
                    self.simpleAlert("Alert", message: "Please Enter Valid Class Name.")
                }else{
                    let index = textField.text!.startIndex.advancedBy(3)
                    let res = self.containsOnlyLetters(textField.text!.substringToIndex(index))
                    let badCharacters = NSCharacterSet.decimalDigitCharacterSet().invertedSet
                    if res == true && (textField.text!.substringFromIndex(index)).rangeOfCharacterFromSet(badCharacters) == nil{
                        print("correct")
                        if textField.text == self.arrClassName[indexPath.row] {
                            self.simpleAlert("Message", message: "Class Name Not Changed.")
                        }else{
                            self.hitClassNameApi(indexPath,name: textField.text!)
                        }
                    }else{
                        self.simpleAlert("Alert", message: "Please Enter Valid Class Name.")
                    }
                }
                nameLabel.textColor = UIColor.darkGrayColor()
                imgView2.tintColor = UIColor.darkGrayColor()
            }))
            alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.Default, handler: { (action) -> Void in
                //nothing
                nameLabel.textColor = UIColor.darkGrayColor()
                imgView2.tintColor = UIColor.darkGrayColor()
            }))
            self.presentViewController(alert, animated: true, completion: nil)
        }else{
            print("couldn't find index path")
        }
    }
    func hitClassNameApi(indexPath: NSIndexPath, name: String){
        //clearColor()
        print(indexPath.row)
        print(arrClassID[indexPath.row])
        print(name)
        /*let data = [
            "classid" : arrClassID[indexPath.row],
            "name" : name,
            "Editclass" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "classid" : arrClassID[indexPath.row],
            "name" : name,
            "Editclass" : ""
            ])
            .responseJSON {response in
               // print(response.response)
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let message = JSON["message"] as! String
                            print(message)
                            self.simpleAlert("Alert", message: JSON["message"] as! String)
                            self.hitClassListApi()
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
    }
    func hitClassListApi(){
        //clearColor()
        arrClassID.removeAll()
        arrClassName.removeAll()
        arrProffessorName.removeAll()
       /* let data = [
            "userid" : userID,
            "classlist" : ""
        ]*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "classlist" : ""
            ])
            .responseJSON {response in
                //print(response.response)
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            let message = JSON["message"] as! NSArray
                            print(message)
                            for i in 0 ..< message.count {
                                if let cID = message[i]["classid"] as? String {
                                    self.arrClassID.append(cID)
                                }
                                if let cName = message[i]["name"] as? String {
                                    //let newString = cName.stringByReplacingOccurrencesOfString(" ", withString: "", options: NSStringCompareOptions.LiteralSearch, range: nil)
                                    self.arrClassName.append(cName)
                                }
                                if let pName = message[i]["proffessor"] as? String {
                                    self.arrProffessorName.append(pName)
                                }
                            }//end for loop
                            
                            self.hideHUD()
                            self.collectionView.reloadData()
                        }else{
                            self.hideHUD()
                            //self.simpleAlert("Alert", message: "Sorry, No Class Found.")
                        }
                    }
                  //self.onceOnly = false
                   
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
    }
   /* func clearColor(cell: UICollectionViewCell){
        for i in 0  ..< arrClassName.count {
            let index = NSIndexPath(forRow: i, inSection: 0)
            print(index)
            let cell: UICollectionViewCell = collectionView.cellForItemAtIndexPath(index)!
            let nameLabel: UILabel = (cell.viewWithTag(101) as! UILabel)
            nameLabel.textColor = UIColor.darkGrayColor()
            let imgView2: UIImageView = (cell.viewWithTag(111) as! UIImageView)
            imgView2.tintColor = UIColor.darkGrayColor()
        }
    }*/
    func actionImage(){
        self.performSegueWithIdentifier("segueSettings", sender: self)
    }
    func actionMenu(){
        performSegueWithIdentifier("openMenu", sender: nil)
    }
    @IBAction func edgePanGesture(sender: UIScreenEdgePanGestureRecognizer) {
        let translation = sender.translationInView(view)
        
        let progress = MenuHelper.calculateProgress(translation, viewBounds: view.bounds, direction: .Right)
        MenuHelper.mapGestureStateToInteractor(
            sender.state,
            progress: progress,
            interactor: interactor){
                self.performSegueWithIdentifier("openMenu", sender: nil)
        }
    }
    func actionAddEvent(){
       // self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "Create New Event", style: UIBarButtonItemStyle.Plain, target: nil, action: nil)
        //self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("CourseVC") as! CourseVC, animated: true)
        self.performSegueWithIdentifier("segueCourses", sender: self)
    }
    func actionMyEvent(){
        let nextVC = self.storyboard?.instantiateViewControllerWithIdentifier("MyEventsVC") as! MyEventsVC
    self.navigationController?.pushViewController(nextVC, animated: true)
    nextVC.arrClasses = arrClassName
    }
    func actionMessages(){
        self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("GroupMessagesVC") as! GroupMessagesVC, animated: true)
    }
    func actionHotTopics(){
         self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("HotTopicVC") as! HotTopicVC, animated: true)
    }
    func textField(textField: UITextField, shouldChangeCharactersInRange range: NSRange, replacementString string: String) -> Bool {
        let str = (textField.text! as NSString).stringByReplacingCharactersInRange(range, withString: string.uppercaseString)
        let currentCharacterCount = str.characters.count ?? 0
        if currentCharacterCount >= 7{
            //let myNSString = str as NSString
            //myNSString.substringWithRange(NSRange(location: 0, length: 5))
        }else{
            textField.text = str
        }
        return false
    }
    func containsOnlyLetters(input: String) -> Bool {
        for chr in input.characters {
            if (!(chr >= "A" && chr <= "Z") ) {
                return false
            }
        }
        return true
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "segueSettings"{
            let nextVC = segue.destinationViewController as! SettingsVC
            nextVC.arrClassName = arrClassName
            nextVC.arrClassID = arrClassID
        }
        if let destinationViewController = segue.destinationViewController as? MenuViewController {
            destinationViewController.transitioningDelegate = self
            destinationViewController.interactor = interactor
            destinationViewController.menuActionDelegate = self
        }
        if segue.identifier == "segueCourses"{
            let nextVC = segue.destinationViewController as! CourseVC
            nextVC.arrClassList = arrClassName
        }
        if segue.identifier == "segueAcceptInvite"{
            //let nextVC = segue.destinationViewController as! AcceptInviteVC
            //nextVC.arrClassList = arrClassName
        }
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
extension DialVC: UIViewControllerTransitioningDelegate {
    func animationControllerForPresentedController(presented: UIViewController, presentingController presenting: UIViewController, sourceController source: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return PresentMenuAnimator()
    }
    
    func animationControllerForDismissedController(dismissed: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return DismissMenuAnimator()
    }
    
    func interactionControllerForDismissal(animator: UIViewControllerAnimatedTransitioning) -> UIViewControllerInteractiveTransitioning? {
        return interactor.hasStarted ? interactor : nil
    }
    
    func interactionControllerForPresentation(animator: UIViewControllerAnimatedTransitioning) -> UIViewControllerInteractiveTransitioning? {
        return interactor.hasStarted ? interactor : nil
    }
}
extension DialVC : MenuActionDelegate {
    func openSegue(segueName: String, sender: AnyObject?) {
        dismissViewControllerAnimated(true){
            switch(segueName){
            case "push":
               // self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "Create New Class", style: UIBarButtonItemStyle.Plain, target: nil, action: nil)
                self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("LoginVC") as! LoginVC , animated: true)
            case "segueSettings":
                self.performSegueWithIdentifier(segueName, sender: sender)
                
            case "segueAcceptInvite":
                self.performSegueWithIdentifier(segueName, sender: sender)
            default:
                return
            }
            
        }
    }
}




/*  //let addButton   = UIBarButtonItem(image: UIImage(named: "plus")!,  style: .Done, target: self, action: #selector(MainVC.actionNewEvent))
 //let myEventButton = UIBarButtonItem(image: UIImage(named: "event")!,  style: .Plain, target: self, action: #selector(MainVC.actionMyEvent))
 //let msgButton   = UIBarButtonItem(image: UIImage(named: "messages")!,  style: .Done, target: self, action: #selector(MainVC.actionNewEvent))
 // let topicButton = UIBarButtonItem(image: UIImage(named: "topic")!,  style: .Plain, target: self, action: #selector(MainVC.actionMyEvent))
 //navigationItem.rightBarButtonItems = [topicButton, msgButton, myEventButton, addButton]
 
 //Add Navigation Buttons
 let btnAdd: UIButton = UIButton(type: .Custom)
 
 btnAdd.addTarget(self, action: #selector(DialVC.actionAddEvent), forControlEvents: .TouchUpInside)
 initNavigationItem(btnAdd, title: "Add Event", imgName: "plus")
 let barButtonAdd: UIBarButtonItem = UIBarButtonItem(customView: btnAdd)
 
 let btnEvent: UIButton = UIButton(type: .Custom)
 btnEvent.addTarget(self, action: #selector(DialVC.actionMyEvent), forControlEvents: .TouchUpInside)
 initNavigationItem(btnEvent, title: "My Event", imgName: "event")
 let barButtonEvent: UIBarButtonItem = UIBarButtonItem(customView: btnEvent)
 
 let btnMessages: UIButton = UIButton(type: .Custom)
 btnMessages.addTarget(self, action: #selector(DialVC.actionMessages), forControlEvents: .TouchUpInside)
 initNavigationItem(btnMessages, title: "Messages", imgName: "messages")
 let barButtonMessages: UIBarButtonItem = UIBarButtonItem(customView: btnMessages)
 
 let btnTopics: UIButton = UIButton(type: .Custom)
 btnTopics.addTarget(self, action: #selector(DialVC.actionHotTopics), forControlEvents: .TouchUpInside)
 initNavigationItem(btnTopics, title: "Hot Topics", imgName: "topic")
 let barButtonTopics: UIBarButtonItem = UIBarButtonItem(customView: btnTopics)
 
 self.navigationItem.rightBarButtonItems = [barButtonTopics, barButtonMessages, barButtonEvent, barButtonAdd]
 
 
 let navButton   = UIBarButtonItem(image: UIImage(named: "menu")!,  style: .Done, target: self, action: #selector(MainVC.actionMenu))
 navigationItem.leftBarButtonItem = navButton

 
 */