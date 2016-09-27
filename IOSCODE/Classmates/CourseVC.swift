//
//  CourseVC.swift
//  Classmates
//
//  Created by Mallu on 6/18/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
class CourseVC: UIViewController, UITextFieldDelegate, UIScrollViewDelegate{
    
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var scrolling: UIScrollView!
    @IBOutlet weak var viewClasses: UIView!
    
    var arrClassList = [String]()
    let btn: UIButton = UIButton()
    var classSelected = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
        
        
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
        //self.navigationController?.navigationBar.backItem?.title = "Create New Event"
    }
    func configureDesign(){
        
        //myMutableString = NSMutableAttributedString(string: lblHint.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 12.0)!])
        //myMutableString.addAttribute(NSForegroundColorAttributeName, value: darkBlue, range: NSRange(location:25,length:7))
        //lblHint.attributedText = myMutableString
        //initAppearance(btnNext)
        //initAppearNavigation(viewTop)
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(CourseVC.actionBack)))
        //txtCourseName.attributedPlaceholder = NSAttributedString(string: "Course Name", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
       // txtCourseName.delegate = self
        scrolling.frame.origin.x = 0
        scrolling.frame.size.height = 50
        scrolling.backgroundColor = UIColor.clearColor()
        scrolling.delegate = self
        
        scrolling.showsHorizontalScrollIndicator = false
        scrolling.showsVerticalScrollIndicator = false
        scrolling.scrollEnabled = true
        
        viewClasses.frame.origin.y = 0
        viewClasses.frame.size.height = 50
        viewClasses.backgroundColor = UIColor.clearColor()
        customButton(btn)

        removeClass()
        
    }
    func removeClass(){
        
        if arrClassList.count == 0{
            self.simpleAlert("Alert", message: "No Class Found.")
            //btnRemoveClass.hidden = false
        }else{
            scrolling.hidden = false
            //scrolling.frame.size.width = ((btn.frame.size.width) + 10) * CGFloat(arrClassName.count) - 10
            viewClasses.frame.size.width = ((btn.frame.size.width) + 10) * CGFloat(arrClassList.count) - 10
            print(scrolling.frame.size)
            print(viewClasses.frame.size)
            for i in 0 ..< arrClassList.count{
                let btn1 = UIButton()
                btn1.titleLabel!.font =  UIFont(name: "Ubuntu", size: 10)
                btn1.setTitle(arrClassList[i], forState: UIControlState.Normal)
                btn1.tag = i
                customButton(btn1)
                //scrolling.center.x = self.view.center.x
                scrolling.contentSize = viewClasses.bounds.size
                viewClasses.center.x = self.view.center.x
                self.viewClasses.addSubview(btn1)
                btn1.frame.origin.x = 50
                btn1.frame.origin.x = CGFloat(i) * ((btn1.frame.origin.x) + 10)
                btn1.setTitleColor(colorLightBlue, forState: .Normal)
            }
            if viewClasses.frame.size.width >= view.frame.size.width {
                print("scroll")
                viewClasses.frame.origin.x = 0
            }
        }
        
    }
    func customButton(btn: UIButton){
        btn.titleLabel?.adjustsFontSizeToFitWidth = true
        btn.titleLabel?.textAlignment = .Center
        btn.frame.size.height = 50
        btn.frame.size.width = 50
        btn.addTarget(self, action: #selector(CourseVC.buttonAction(_:)), forControlEvents: UIControlEvents.TouchUpInside)
        btn.backgroundColor = UIColor.whiteColor()
        btn.layer.cornerRadius = btn.frame.size.width/2.0
        btn.clipsToBounds = true
        btn.titleLabel?.textColor = colorLightBlue
    }
    func buttonAction(sender: UIButton!) {
        classSelected = arrClassList[sender.tag]
        self.performSegueWithIdentifier("segueTopic", sender: self)
        
    }
   /* @IBAction func actionNext(sender: AnyObject) {
       //next()
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        next()
        return true
    }
    func next(){
        if arrClassList.count == 0 {
            self.simpleAlert("Alert", message: "No Class Added")
        }else{
            counter = range(txtCourseName)
            if counter.characters.count == 0 {
                self.simpleAlert("Alert", message: "Please Enter Course Name!")
            }else if counter.characters.count != 6{
                self.simpleAlert("Alert", message: "Please Enter Valid Course Name!")
            }else{
                if arrClassList.contains(txtCourseName.text!){
                     self.performSegueWithIdentifier("segueTopic", sender: self)
                }else{
                     self.simpleAlert("Alert", message: "Course Name Does Not Exist!")
                }
            }
        }
       
        //self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("TopicVC") as! TopicVC, animated: true)
    }*/
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let nextVC = segue.destinationViewController as! TopicVC
        nextVC.courseName = classSelected
        
    }
   /* func textField(textField: UITextField, shouldChangeCharactersInRange range: NSRange, replacementString string: String) -> Bool {
        
        let currentCharacterCount = textField.text?.characters.count ?? 0
        if (range.length + range.location > currentCharacterCount){
            return false
        }
        let newLength = currentCharacterCount + string.characters.count - range.length
        return newLength <= 6
    }*/
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.view.endEditing(true)
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}