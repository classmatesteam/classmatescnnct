//
//  MyEventTableViewCell.swift
//  Classmates
//
//  Created by Mallu on 6/22/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
class MyEventsTableViewCell: UITableViewCell{
    
    @IBOutlet weak var itemUserName: UILabel!
    @IBOutlet weak var btnInvite: UIButton!
    @IBOutlet weak var btnDelete: UIButton!
    @IBOutlet weak var itemParticipants: UIButton!
    @IBOutlet weak var itemViewTop: UIView!
    @IBOutlet weak var itemImg: UIImageView!
    @IBOutlet weak var itemDate: UILabel!
    @IBOutlet weak var itemCourse: UILabel!
    @IBOutlet weak var itemTopic: UILabel!
    @IBOutlet weak var itemTime: UILabel!
    @IBOutlet weak var itemVenue: UILabel!
    @IBOutlet weak var itemImgInvite: UIImageView!
    @IBOutlet weak var itemImgDelete: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        itemDate.adjustsFontSizeToFitWidth = true
        itemParticipants.layer.cornerRadius = 7.0
        itemParticipants.clipsToBounds = true
        itemImg.layer.cornerRadius = itemImg.frame.size.width/2.0
        itemImg.clipsToBounds = true
        itemTopic.adjustsFontSizeToFitWidth = true
        initAppearNavigation(itemViewTop)
    }
}