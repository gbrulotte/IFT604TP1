using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace HockeyWeb.Controllers
{
    public class BaseController : Controller
    {
        protected const string UserIdCookieName = "user_id";
        protected Guid UserId;

        protected void AddCookie(string key, string value)
        {
            var cookie = new HttpCookie(key)
            {
                Value = value
            };
            Response.Cookies.Add(cookie);
        }

        protected void DeleteCookie(string key)
        {
            var cookie = new HttpCookie(key)
            {
                Expires = DateTime.Now.AddDays(-1)
            };
            Response.Cookies.Add(cookie);
        }

        protected string GetCookie(string key)
        {
            var cookie = Request.Cookies[key];
            return cookie != null ? cookie.Value : null;
        }

        protected override void OnActionExecuting(ActionExecutingContext filterContext)
        {
            var userId = GetCookie(UserIdCookieName);
            if (userId == null)
            {
                UserId = Guid.NewGuid();
                AddCookie(UserIdCookieName, UserId.ToString());
            }
            else
            {
                UserId = Guid.Parse(userId);
            }
        }
    }
}
