//
//  ConfigureButton.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
class ConfigureButton: UIButton {
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
        self.layer.cornerRadius = UIScreen.mainScreen().bounds.height / 30.0
        self.clipsToBounds = true
        self.backgroundColor = colorTop
        self.tintColor = UIColor.whiteColor()
    }
}
