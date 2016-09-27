//
//  WebLoginVC.swift
//  Classmates
//
//  Created by Mallu on 6/21/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Kanna
import Alamofire
class WebLoginVC: UIViewController, UIWebViewDelegate{
    
    
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var webView: UIWebView!
    
    //var userID = ""
    var onceOnly: Bool = false
    override func viewDidLoad() {
        super.viewDidLoad()
        loadUrl()
        
    }
    func loadUrl(){
        userID = defaults.stringForKey("userID")!
        webView.delegate = self
        let url = NSURL(string: "https://webapp4.asu.edu/myasu/student/schedule?term=2167")
        webView.loadRequest(NSURLRequest(URL: url!))
        
       // initAppearNavigation(viewTop)
        webView.frame.origin.y = (self.viewTop?.frame.origin.y)! + (self.viewTop.frame.size.height)
        webView.frame.size.height = UIScreen.mainScreen().bounds.height - ((self.viewTop.frame.origin.y) + (self.viewTop.frame.size.height))
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(WebLoginVC.actionBack)))
    }
    func webViewDidStartLoad(webView: UIWebView) {
        print("start")
        self.showHUD()
        let urlString = webView.request?.URL?.absoluteString
        if urlString == "https://webapp4.asu.edu/myasu/student/schedule?term=2167" {
            webView.hidden = true
        }
    }
    func webViewDidFinishLoad(webView: UIWebView) {
        print("finish")
        self.hideHUD()
        //let url2 = "https://weblogin.asu.edu/cas/login?service=https%3A%2F%2Fweblogin.asu.edu%2Fcgi-bin%2Fcas-login%3Fcallapp%3Dhttps%253A%252F%252Fwebapp4.asu.edu%252Fmyasu%252Fstudent%252Fschedule%253Finit%253Dfalse%2526term%253D2167"
        let urlString = webView.request?.URL?.absoluteString
        //let url = "\(urlString)"
        print(urlString)
        var arrID = [String]()
        var arrName = [String]()
        if urlString == "https://webapp4.asu.edu/myasu/student/schedule?term=2167" {
            print("url")
            let str = webView.stringByEvaluatingJavaScriptFromString("document.documentElement.outerHTML")
            
            if let doc = Kanna.HTML(html: str!, encoding: NSUTF8StringEncoding) {
                let bodyNode  = doc.body
                var nod1 = ""
                var nod2 = ""
                //*[@id="asu_content_container"]/div/div[1]/div/div[2]/div[1]/div[2]/table/tbody[1]/tr/td[2]/a
                //*[@id="asu_content_container"]/div/div[1]/div/div[2]/div[1]/div[2]/table/tbody[2]/tr/td[2]/a
                for i in 1..<15{
                    if let inputNodes = bodyNode?.xpath("//*[@id='asu_content_container']/div/div[1]/div/div[2]/div[1]/div[2]/table/tbody[\(i)]/tr/td[2]/a") {

                        for node in inputNodes {
                            nod1 = (node.text)!.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
                            print(nod1)
                
                            let words: NSArray = nod1.componentsSeparatedByCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
                            let nospacestring: String = words.componentsJoinedByString("")
                            print(nospacestring)
                            if nospacestring != ""{
                                arrID.append(nospacestring)
                            }
                        }
                    }
                    //*[@id="asu_content_container"]/div/div[1]/div/div[2]/div[1]/div[2]/table/tbody[1]/tr/td[5]/a
                    if let inputNodes = bodyNode?.xpath("//*[@id='asu_content_container']/div/div[1]/div/div[2]/div[1]/div[2]/table/tbody[\(i)]/tr/td[5]/a") {
                        for node in inputNodes {
                            print("Y")
                            print(node.text)
                            nod2 = (node.text)!.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet())
                            
                            if nod2 != "" && nod2 != "iCourse"{
                                arrName.append(nod2)
                            }
                            
                        }
                    }
                }
            }
            print(arrID)
            print(arrName)
            hitApi(arrID,name: arrName)
        }
    }
    func hitApi(id: [String], name: [String]){
        print(id)
        print(name)
        let strID = id.joinWithSeparator(",") // "1-2-3"
        let strName = name.joinWithSeparator(",")
        if !onceOnly{
        onceOnly = true
        self.showHUD()
            Alamofire.request(.POST, apiLink, parameters: [
                "userid" : userID,
                "name" : strID,
                "pname": strName,
                "unilogin" : ""
                ])
            .responseJSON {response in
                if String(response.result) == "SUCCESS"{
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            self.hideHUD()
                            self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("DialVC") as! DialVC, animated: true)
                        }else{
                            self.hideHUD()
                            self.simpleAlert("Alert", message: JSON["message"] as! String)
                        }
                    }
                    //self.onceOnly = false
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api
        }
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}


 // if let inputNodes = bodyNode?.xpath("/html/body/div[@id='asu_container']/div[@id='asu_content']/div[@id='asu_content_well']/div[@id='asu_content_container']/div[@class='asu_body_copy myasu-container']/table[@class='column-container']/tbody/tr/td[@class='content-column']/div[@class='schedule']/div[@class='box']/table/tbody/tr/td[2]") {

//if let inputNodes = bodyNode?.xpath("/html/body/div[@id='asu_container']/div[@id='asu_content']/div[@id='asu_content_well']/div[@id='asu_content_container']/div[@class='asu_body_copy myasu-container']/table[@class='column-container']/tbody/tr/td[@class='content-column']/div[@class='schedule']/div[@class='box']/table/tbody/tr/td[5]") {