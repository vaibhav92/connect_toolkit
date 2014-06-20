VERSION 5.00
Begin VB.Form Form2 
   Caption         =   "Messages"
   ClientHeight    =   3840
   ClientLeft      =   60
   ClientTop       =   450
   ClientWidth     =   6075
   LinkTopic       =   "Form2"
   ScaleHeight     =   3840
   ScaleWidth      =   6075
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  'Windows Default
   Begin VB.TextBox Text1 
      Height          =   6015
      Left            =   0
      Locked          =   -1  'True
      MultiLine       =   -1  'True
      ScrollBars      =   2  'Vertical
      TabIndex        =   0
      Top             =   0
      Width           =   5415
   End
End
Attribute VB_Name = "Form2"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub Form_Resize()
Text1.Height = Me.Height - 550
Text1.Width = Me.Width - 200
End Sub
