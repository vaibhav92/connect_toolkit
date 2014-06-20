VERSION 5.00
Object = "{248DD890-BB45-11CF-9ABC-0080C7E7B78D}#1.0#0"; "MSWINSCK.OCX"
Object = "{67397AA1-7FB1-11D0-B148-00A0C922E820}#6.0#0"; "MSADODC.OCX"
Begin VB.Form Form3 
   Caption         =   "SMSSender"
   ClientHeight    =   2550
   ClientLeft      =   990
   ClientTop       =   1695
   ClientWidth     =   4155
   LinkTopic       =   "Form3"
   MaxButton       =   0   'False
   ScaleHeight     =   2550
   ScaleWidth      =   4155
   Begin VB.ComboBox DataCombo1 
      Height          =   315
      Left            =   120
      TabIndex        =   4
      Top             =   120
      Width           =   2535
   End
   Begin VB.TextBox Text1 
      BackColor       =   &H8000000F&
      Height          =   732
      Left            =   120
      Locked          =   -1  'True
      MultiLine       =   -1  'True
      ScrollBars      =   2  'Vertical
      TabIndex        =   2
      Top             =   1680
      Width           =   3852
   End
   Begin VB.CommandButton Command1 
      Caption         =   "send"
      Default         =   -1  'True
      Enabled         =   0   'False
      Height          =   288
      Left            =   2880
      TabIndex        =   1
      Top             =   120
      Width           =   1092
   End
   Begin VB.TextBox Text2 
      Height          =   732
      Left            =   120
      TabIndex        =   0
      Top             =   480
      Width           =   3852
   End
   Begin MSWinsockLib.Winsock recclient 
      Index           =   0
      Left            =   0
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
   End
   Begin MSAdodcLib.Adodc Adodc1 
      Height          =   336
      Left            =   1440
      Top             =   840
      Width           =   1572
      _ExtentX        =   2778
      _ExtentY        =   582
      ConnectMode     =   0
      CursorLocation  =   3
      IsolationLevel  =   -1
      ConnectionTimeout=   15
      CommandTimeout  =   30
      CursorType      =   3
      LockType        =   3
      CommandType     =   2
      CursorOptions   =   0
      CacheSize       =   50
      MaxRecords      =   0
      BOFAction       =   0
      EOFAction       =   0
      ConnectStringType=   3
      Appearance      =   1
      BackColor       =   -2147483643
      ForeColor       =   -2147483640
      Orientation     =   0
      Enabled         =   -1
      Connect         =   "DSN=application"
      OLEDBString     =   ""
      OLEDBFile       =   ""
      DataSourceName  =   "application"
      OtherAttributes =   ""
      UserName        =   ""
      Password        =   ""
      RecordSource    =   "phono"
      Caption         =   "Adodc1"
      BeginProperty Font {0BE35203-8F91-11CE-9DE3-00AA004BB851} 
         Name            =   "MS Sans Serif"
         Size            =   8.25
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      _Version        =   393216
   End
   Begin VB.Label Label1 
      Caption         =   "&Incoming Messages"
      Height          =   252
      Left            =   120
      TabIndex        =   3
      Top             =   1320
      Width           =   2052
   End
End
Attribute VB_Name = "Form3"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Dim currindex As Integer


Private Sub Command1_Click()
Dim msg As String, i As Integer
msg = DataCombo1.Text  '.boundtext
msg = msg & " " & Text2.Text
msg = msg & Chr(0)
For i = 1 To recclient.Count - 1
recclient(i).SendData msg
Next
End Sub

Public Sub updatecontrols()

Command1.Enabled = False
DataCombo1.Enabled = False
Text2.Enabled = False
'Text1.Enabled = False


If recclient.Count > 1 Then
Command1.Enabled = True
DataCombo1.Enabled = True
Text2.Enabled = True
'Text1.Enabled = True
End If


End Sub

Private Sub Form_Load()
updatecontrols
End Sub

Private Sub Form_Resize()
On Error Resume Next
Text2.Width = Me.Width - Text2.Left - 200
Text1.Width = Text2.Width
Command1.Left = Text2.Width + Text2.Left - Command1.Width
DataCombo1.Width = Command1.Left - DataCombo1.Left - 100
Text1.Height = Height - Text1.Top - 750
End Sub

Private Sub Form_Unload(Cancel As Integer)
End
End Sub

Private Sub Label1_Click()
Dim i As Long
For i = 0 To 100
Dim r As Integer
r = rnd() * 2 + 1
If r = 1 Then Text2.Text = "a "
If r = 2 Then Text2.Text = "b "
If r = 3 Then Text2.Text = "c "
'Text2.Text = i & "sdfksjfhjkdhsfjksncjdsfhjiewruyiuenkjsdcnksjfdsf"
Command1_Click
Next
End Sub

Private Sub recclient_Close(Index As Integer)
Unload recclient(Index)
updatecontrols
End Sub

Private Sub Text1_MouseUp(Button As Integer, Shift As Integer, X As Single, Y As Single)
If Button = vbRightButton Then Text1.Text = ""


End Sub
