VERSION 5.00
Object = "{248DD890-BB45-11CF-9ABC-0080C7E7B78D}#1.0#0"; "MSWINSCK.OCX"
Begin VB.Form Form1 
   Caption         =   "Form1"
   ClientHeight    =   1740
   ClientLeft      =   6435
   ClientTop       =   2070
   ClientWidth     =   5550
   LinkTopic       =   "Form1"
   ScaleHeight     =   1740
   ScaleWidth      =   5550
   Begin VB.Timer Timer1 
      Interval        =   2000
      Left            =   5160
      Top             =   0
   End
   Begin MSWinsockLib.Winsock sendclient 
      Index           =   0
      Left            =   4680
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
   End
   Begin MSWinsockLib.Winsock timerclient 
      Index           =   0
      Left            =   3240
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
   End
   Begin MSWinsockLib.Winsock Wsksmsrec 
      Left            =   3600
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
      LocalPort       =   9021
   End
   Begin MSWinsockLib.Winsock Wsksmssend 
      Left            =   4320
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
      LocalPort       =   9023
   End
   Begin MSWinsockLib.Winsock Wsktimer 
      Left            =   3960
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
      LocalPort       =   9020
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit
Dim buff As String
Private Sub Form_Load()
Randomize
Wsktimer.Listen
Wsksmssend.Listen
Wsksmsrec.Listen
Form3.Show
Me.Hide
End Sub

Private Sub sendclient_Close(Index As Integer)
Unload sendclient(Index)
End Sub

Private Sub sendclient_DataArrival(Index As Integer, ByVal bytesTotal As Long)
Dim data As String, ind As Integer
sendclient(Index).GetData data
buff = buff & data
ind = InStr(1, buff, Chr(10), vbBinaryCompare)
If ind <= 0 Then Exit Sub
'Form3.Text1.Text = Form3.Text1.Text & vbNewLine & Left(data, ind - 2)
Form3.Text1.Text = Form3.Text1.Text & vbNewLine & data
'data = Mid(data, ind + 1)
Form3.Text1.SelStart = Len(Form3.Text1.Text)
Form3.Text1.SelLength = 1
Form3.Show
End Sub

Private Sub Timer1_Timer()
On Error Resume Next
Dim i As Integer, tagtype As Integer, flindx As Integer, locid As Integer
Dim density As String
tagtype = Int(Rnd() * 7)
flindx = Int(Rnd() * 3)
locid = Int(Rnd() * 3) + 2
For i = 1 To timerclient.Count - 1
Select Case tagtype
Case 0: Call timerclient(i).SendData("TEXTTAG " & locid & ",This is the text tag" & flindx & Chr(13) & Chr(10))
Case 1: Call timerclient(i).SendData("IMAGETAG " & locid & ",image" & flindx & ".jpg" & Chr(13) & Chr(10))
Case 2: If (flindx = 0) Then density = "sparse"
        If (flindx = 1) Then density = "moderate"
        If (flindx = 2) Then density = "dense"
        Call timerclient(i).SendData("TRAFFICTAG " & locid & "," & density & Chr(13) & Chr(10))
Case 3: Call timerclient(i).SendData("AUDIOTAG " & locid & ",audio" & flindx & ".wav" & Chr(13) & Chr(10))
Case 4: Call timerclient(i).SendData("VEDIOTAG " & locid & ",vedio" & flindx & ".3gp" & Chr(13) & Chr(10))
Case 5: Call timerclient(i).SendData("TEXTTAG " & locid & ",This is the text tag" & flindx & Chr(13) & Chr(10))
Case 6: Call timerclient(i).SendData("IMAGETAG " & locid & ",image" & flindx & ".jpg" & Chr(13) & Chr(10))
Case 7: If (flindx = 0) Then density = "sparse"
        If (flindx = 1) Then density = "moderate"
        If (flindx = 2) Then density = "dense"
        Call timerclient(i).SendData("TRAFFICTAG " & locid & "," & density & Chr(13) & Chr(10))
End Select
Next
End Sub

Private Sub timerclient_Close(Index As Integer)
Unload timerclient(Index)
End Sub

Private Sub Wsksmsrec_ConnectionRequest(ByVal requestID As Long)
Load Form3.recclient(Form3.recclient.Count)
Form3.recclient(Form3.recclient.Count - 1).Accept (requestID)
Form3.updatecontrols
End Sub


Private Sub Wsksmssend_ConnectionRequest(ByVal requestID As Long)
Load sendclient(sendclient.Count)
sendclient(sendclient.Count - 1).Accept (requestID)
End Sub

Private Sub Wsktimer_ConnectionRequest(ByVal requestID As Long)
Load timerclient(timerclient.Count)
timerclient(timerclient.Count - 1).Accept (requestID)
End Sub

