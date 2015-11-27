using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Web.Script.Serialization;

namespace HockeyWeb.Models
{
    public class ParisViewModel
    {
        public Guid MatchId { get; set; }

        public Guid UserId { get; set; }

        public string Team { get; set; }

        public int Amount { get; set; }

        public dynamic Result { get; set; }

        public Task InnerTask { get; private set; }

        public void SendBet()
        {
            try
            {
                var clientSocket = new TcpClient("localhost", 8081);
                var ns = clientSocket.GetStream();
                var sendBytes = Encoding.ASCII.GetBytes(string.Format("Bet~{0}~{1}~{2}\n", MatchId, Team, Amount));

                ns.Write(sendBytes, 0, sendBytes.Length);

                InnerTask = Task.Factory.StartNew(() =>
                {
                    var bytes = new byte[1024];
                    var isDone = false;
                    var result = string.Empty;
                    while (!isDone)
                    {
                        var bytesRead = ns.Read(bytes, 0, bytes.Length);
                        var returnData = Encoding.ASCII.GetString(bytes, 0, bytesRead);
                        result += returnData;
                        if (result.Last() == '\n') result = result.Remove(result.Length - 1);
                        isDone = bytesRead > 0 && result.Last() == '}';
                    }
                    
                    clientSocket.Close();
                    Result = new JavaScriptSerializer().Deserialize<dynamic>(result);
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }
    }
}