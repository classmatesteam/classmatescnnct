//
//  MenuTableViewCell.swift
//  Classmates
//
//  Created by Mallu on 6/17/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit

class MenuTableViewCell: UITableViewCell{
    
    
    @IBOutlet weak var imgCell: UIImageView!
    @IBOutlet weak var lblCell: UILabel!
    @IBOutlet weak var itemCount: UILabel!
    override func awakeFromNib() {
        lblCell.textColor = UIColor.grayColor()
        itemCount.layer.cornerRadius = itemCount.frame.size.width/2.0
        itemCount.clipsToBounds = true
        itemCount.frame.origin.x = imgCell.frame.origin.x + imgCell.frame.size.width - 15
    }
}